package mp3.problem1;

/*
 * Expects each readInputs call to be a state change (and not a sequence).
 * Such transition can have infinite string length consisting of "e", "b", or "m".
 * These characters should all boil down to 1 instance each (e.g: "eebbbm" = "ebm")
 * E.g: From Awake state, input "ebm" would get me to deep sleep since, e takes preference over m, and b has no effect at init state if e is present. So "ebm" = "e"
 * A single inputs' chars are commutative... "bem" = "ebm" etc.
 */
 
public class SleepCycle {
    private State state;
    
    enum State {
        // [MAYBE-TODO] can change these lines but do *not* change names
        Awake,
        LightSleep,
        DeepSleep
    }

    // Students like constructors
    public SleepCycle() {
	state = State.Awake;
    }

    // The FSM is somewhat unusual and can take multiple inputs at once.
    // For example, if the string 's' is "em", it means both "e" and "m" are inputs at once,
    // and not that the input is a sequence of first input "e" and then input "m".
    public void readInputs(String s) throws SleepCycleInvalidInputs {
	//remove all whitespace
	s = s.replaceAll("\\s+","");
	s = s.toLowerCase();
	int[] count = new int[]{0,0,0}; //b,m,e
	for (int i = 0; i < s.length(); i++)
	    if (s.charAt(i) == 'b')
		count[0]++;
	    else if (s.charAt(i) == 'm')
		count[1]++;
	    else if (s.charAt(i) == 'e')
		count[2]++;
	    else
		throw new SleepCycleInvalidInputs("Input may only contain characters: b, m, e");
        
	//ACCEPTS EMPTY INPUT
	/* " " */
	if (s=="")
	    return; //NO STATE CHANGE ASSUMPTION (3.2)
	
	if (count[2]>0 && count[1]>0)
	    count[1]=0; //(4)
	
	switch(state) {
	case Awake:
	    if(count[0]>0) {//b
		/*b,bm,be,bme*/ //(1.3)
		return; //PRECEDENCE OVER M ASSUMED //PRECEDENCE OVER E DURING AWAKE ASSUMED
	    }
	    else if(count[1]>0) {//m, !b (2.1)
		/*m*/
		state=State.LightSleep;
	    }
	    else {
		/*e,me*/
		state = State.DeepSleep;//(3.1)
	    }
	    
	    break;
	    
	case LightSleep:
	    if(count[0]>0) {//b
		/*b,bm,be,bme*/
		if(count[2]==0) {//b, !e (1.2)
		    /*b,bm*/
		    state = State.Awake;
		    return;
		}
		else {
		    /*be,bme*/
		    return; //NO STATE CHANGE ASSUMED srsly what do we do with bme here...
		}
	    }
	    else if(count[1]>0) { //!b, m, (!e) PRECEDENCE OVER M ASSUMED (5.1)
		/*m*/
		state = State.DeepSleep;
	    }
	    else{
		/*e,me*/
		return; //NO STATE CHANGE ASSUMED
	    }
	    break;
	    
	case DeepSleep:
	    if(count[2]>0) { //e
		/*e,me,be,bme*/ //(3.2)
		return;
	    }
	    if(count[0]>0) {//b, !e (1.2)
		/*b,bm*/
		state = State.Awake;
		return;
	    }
	    else if(count[1]>0) { //!b, m, (!e) PRECEDENCE OVER M ASSUMED
		/*m*/
		return; //(5.2)
	    }
	    break;
	}
	
    }

    // We want to know the state
    public SleepCycle.State getState() {
        return state;
    }
}
