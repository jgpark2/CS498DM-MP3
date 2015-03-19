package mp3.problem1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static mp3.problem1.SleepCycle.State.*;
import java.util.*;

public class SleepCycleTest {
    SleepCycle sc = new SleepCycle();

    /*
     * Expects each readInputs call to be a state change.
     * Such transition can have infinite string length consisting of "e", "b", or "m".
     * These characters should all boil down to 1 instance each (e.g: "eebbbm" = "ebm")
     * E.g: From Awak state, input "ebm" would get me to deep sleep since, e takes preference over m, and b has no effect at init state if e is present. So "ebm" = "e"
     * A single inputs' chars are commutative... "bem" = "ebm" etc.
     */

    public void assertState(SleepCycle.State e_s, SleepCycle.State s) {
	assertEquals(e_s,s);
	System.out.println("AAAAAAAAAAAA");
    }
    
    public void assertPostState(SleepCycle sc1, String input1, SleepCycle sc2, String input2) {
	sc1.readInputs(input1);
        sc2.readInputs(input2);
        assertState(sc1.getState(),sc2.getState());
    }
    
    public String generateRandomInputs(String minimumReq) {
	// General for any sequence of inputs and state
        Random rand = new Random();
	String randomized = minimumReq;
	int rb = rand.nextInt(6); //Generates 0~5 characters
	int re = rand.nextInt(6);
	int rm = rand.nextInt(6);
	for(int i=0; i<rb; ++i)
	    randomized+="b";
	for(int i=0; i<re; ++i)
	    randomized+="e";
	for(int i=0; i<rm; ++i)
	    randomized+="m";
	char[] inputs = randomized.toCharArray();
	List<Character> inputs_l = new ArrayList<Character>();
	for(char c : inputs)
	    inputs_l.add(c);
	Collections.shuffle(inputs_l);
	
	StringBuilder result = new StringBuilder(inputs_l.size());
	for(Character c : inputs_l) {
	    result.append(c);
	    //System.out.println(c);
	}
	
	return result.toString();
    }

    @Test
    public void startingState() {
        // Starting State should be Awake
	assertState(Awake,sc.getState());
    }

    @Test(expected = Exception.class)
    public void invalidInput1() {
        // This should throw an exception because 'x' is neither 'b' nor 'e' nor 'm'
        sc.readInputs("x");
    }

    public void eTakesPrecedenceInstance() {
	String randomizedInput = generateRandomInputs("e");
	// both "e" and "m" are seen at once; could have been written as sc.readInputs("me")
	assertPostState(new SleepCycle(), randomizedInput, new SleepCycle(), "e");
    }
    
    //Fails on certain randomization if you do not assume b takes precedence over e within the Awake state
    @Test
    public void eTakesPrecedence() {
	//Repeat randomzied test 3 times, all must pass
	eTakesPrecedenceInstance();
	eTakesPrecedenceInstance();
	eTakesPrecedenceInstance();
        
	//In case we *need* to be deterministic...
	assertPostState(new SleepCycle(), "em", new SleepCycle(), "e");
    }

    /*
      public void sequenceOfInputsInstance() {
      // A sequence of inputs should be the same as its conglomerate, eg: sc.readInputs("em")
      String inputs = generateRandomInputs("bb"); //force 2 b's just to prevent 0 length and 1 length
      
      SleepCycle ac = new SleepCycle();
    ac.readInputs(inputs.toCharArray()[0]);
    
    assertPostState(new SleepCycle(), inputs, ac, inputs.substring(1));

    for(char c : inputs.toCharArray()){
    sc.readInputs(""+c);
}
    assertEquals(ac.getState(), sc.getState());
}*/
    
    @Test
    public void sequenceOfInputs1() {
	/*
        //Repeat randomzied test 3 times, all must pass
	sequenceOfInputsInstance();
	sequenceOfInputsInstance();
	sequenceOfInputsInstance();*/
	
	//"e" then "m" is the same as "em" due to e taking precedence.
	sc = new SleepCycle();
	sc.readInputs("e");
	assertPostState(sc, "m", new SleepCycle(), "em");
	
	
    }
    
    @Test
    public void sequenceOfInputs2() {
	//"m" then "b" is the same as "mb" due to being awake as long as b is an input (w/o e)
	sc = new SleepCycle();
	sc.readInputs("m");
	assertPostState(sc, "b", new SleepCycle(), "mb");
    }
    
    @Test
    public void emptyInputNoChange() {
	sc.readInputs("");
	assertState(Awake,sc.getState());
	sc.readInputs("e");
	sc.readInputs("");
	sc.readInputs("");
	assertState(DeepSleep,sc.getState());
    }
    
    //Against different implementation
    @Test(expected = Exception.class)
    public void emptyInputException() {
	sc.readInputs("");
    }
    
    @Test
    public void stateUnchanged() {
	//Other operations do not misbehave and modify sleep cycle object and its state unknowingly
	SleepCycle.State oldState = sc.getState();
	SleepCycle ac = new SleepCycle();
	assertState(oldState, sc.getState());
	sc.readInputs("b");
	sc.readInputs("b");
	sc.readInputs("b");
	sc.readInputs("e");
	assertNotEquals(oldState, sc.getState());
	oldState = sc.getState();
	ac.readInputs("ebebe");
	ac.readInputs("mbmbm");
	assertState(oldState,sc.getState());
	
    }
    
    @Test
    public void staysAwake() {
	sc.readInputs("b");
	SleepCycle ac = new SleepCycle();
	assertState(Awake, sc.getState());
	sc.readInputs("b");
	sc.readInputs("b");
	sc.readInputs("b");
	sc.readInputs("b");
	ac.readInputs("em");
	assertState(Awake, sc.getState());
    }
    
    @Test
    public void undefinedAwakeBM1() {
	//FSM description is too vague for receiving "b" and "m" at the same time during Awake state
	sc.readInputs("bm");
	assertState(Awake,sc.getState());
    }
    
    @Test
    public void undefinedAwakeBM2() {
	//FSM description is too vague for receiving "b" and "m" at the same time during Awake state
	sc.readInputs("bm");
	assertState(LightSleep,sc.getState());
    }
    
    @Test
    public void undefinedLightSleepBM1() {
	//FSM description is too vague for receiving "b" and "m" at the same time during LightSleep state
	sc.readInputs("bm");
	assertState(DeepSleep,sc.getState());
    }
    
    @Test
    public void undefinedLightSleepBM2() {
	//FSM description is too vague for receiving "b" and "m" at the same time during LightSleep state
	sc.readInputs("bm");
	assertState(Awake,sc.getState());
    }
    
    @Test
    public void undefinedLightSleepE1() {
	//FSM description is too vague for receiving "e" or "em" during LightSleep state
	sc.readInputs("e");
	assertState(DeepSleep,sc.getState());
    }
    
    @Test
    public void undefinedLightSleepE2() {
	//FSM description is too vague for receiving "e" or "em" during LightSleep state
	sc.readInputs("e");
	assertState(LightSleep,sc.getState());
    }
}
