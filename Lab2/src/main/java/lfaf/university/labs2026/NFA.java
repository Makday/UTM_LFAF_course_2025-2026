package lfaf.university.labs2026;

import java.util.*;

public class NFA extends FiniteAutomaton {
    private final Map<State, Map<Character, Set<State>>> transitions;

    public NFA(Set<State> states, Set<Character> alphabet, State startState,
               Set<State> acceptStates, Map<State, Map<Character, Set<State>>> transitions) {
        super(states, alphabet, startState, acceptStates);
        this.transitions = transitions;
    }

    @Override
    public boolean accepts(final String inputString)
    {
        Set<State> currentStates = new HashSet<>();
        currentStates.add(startState);

        for(int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                if (transitions.containsKey(state) && transitions.get(state).containsKey(c))
                    nextStates.addAll(transitions.get(state).get(c));
            }
            currentStates = nextStates;
            if (currentStates.isEmpty()) return false;
        }

        for (State state : currentStates) {
            if (acceptStates.contains(state)) return true;
        }

        return false;
    }

    @Override
    public Grammar toRegularGrammar() {
        Set<String> VN = new HashSet<>();
        for (State s : states) VN.add(s.getName());
        Set<Character> VT = alphabet;
        String S = startState.getName();
        List<Production> P = new ArrayList<>();

        for (Map.Entry<State, Map<Character, Set<State>>> entry : transitions.entrySet()) {
            State from = entry.getKey();
            for (Map.Entry<Character, Set<State>> t : entry.getValue().entrySet()) {
                char terminal = t.getKey();
                for (State to : t.getValue()) {
                    P.add(new Production(from.getName(), terminal + to.getName()));
                }
            }
        }

        for (State accept : acceptStates) {
            P.add(new Production(accept.getName(), ""));
        }

        return new Grammar(VN, VT, S, P);
    }

    @Override
    public boolean isDeterministic() {
        for (Map.Entry<State, Map<Character, Set<State>>> entry : transitions.entrySet()) {
            for (Map.Entry<Character, Set<State>> t : entry.getValue().entrySet()) {
                if (t.getKey() == '\0') return false;
                if (t.getValue().size() > 1) return false;
            }
        }
        return true;
    }
}
