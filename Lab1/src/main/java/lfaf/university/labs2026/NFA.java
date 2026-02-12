package lfaf.university.labs2026;

import java.util.*;

public class NFA {
    private Set<Character> Q;
    private Set<Character> E;
    private Map<Character, Map<Character, Set<Character>>> delta;
    private Character q0;
    private Set<Character> F;

    NFA(Set<Character> Q, Set<Character> E, Map<Character, Map<Character, Set<Character>>> delta, Character q0, Set<Character> F){
        this.Q = Q;
        this.E = E;
        this.delta = delta;
        this.q0 = q0;
        this.F = F;
    }

//    public boolean stringBelongToLanguage(final String inputString)
//    {
//        char state = q0;
//        for(int i = 0; i < inputString.length(); i++){
//            char c = inputString.charAt(i);
//            if(!delta.containsKey(state) || !delta.get(state).containsKey(c)) return false;
//            state = delta.get(state).get(c);
//        }
//        return F.contains(state);
//    }

    public boolean stringBelongToLanguage(final String inputString)
    {
        Set<Character> currentStates = new HashSet<>();
        currentStates.add(q0);

        for(int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            Set<Character> nextStates = new HashSet<>();
            for (char state : currentStates) {
                if (delta.containsKey(state) && delta.get(state).containsKey(c))
                    nextStates.addAll(delta.get(state).get(c));
            }
            currentStates = nextStates;
            if (currentStates.isEmpty()) return false;
        }

        for (char state : currentStates) {
            if (F.contains(state)) return true;
        }

        return false;
    }
}
