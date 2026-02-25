package lfaf.university.labs2026;

import java.util.*;

public class DFA extends FiniteAutomaton {
    private final Map<State, Map<Character, State>> transitions;

    public DFA(Set<State> states, Set<Character> alphabet, State startState,
               Set<State> acceptStates, Map<State, Map<Character, State>> transitions) {
        super(states, alphabet, startState, acceptStates);
        this.transitions = transitions;
    }

    @Override
    public boolean accepts(String input) {
        State current = startState;
        for (char c : input.toCharArray()) {
            Map<Character, State> stateTransitions = transitions.get(current);
            if (stateTransitions == null) return false;

            current = stateTransitions.get(c);
            if (current == null) return false;
        }
        return acceptStates.contains(current);
    }

    @Override
    public Grammar toRegularGrammar() {
        Set<String> VN = new HashSet<>();
        for (State s : states) VN.add(s.getName());

        Set<Character> VT = alphabet;

        String S = startState.getName();

        List<Production> P = new ArrayList<>();

        for (Map.Entry<State, Map<Character, State>> entry : transitions.entrySet()) {
            State from = entry.getKey();
            for (Map.Entry<Character, State> t : entry.getValue().entrySet()) {
                char terminal = t.getKey();
                State to = t.getValue();
                P.add(new Production(from.getName(), terminal + to.getName()));
            }
        }

        return new Grammar(VN, VT, S, P);
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

    @Override
    public DFA toDFA() {
        return this;
    }
}
