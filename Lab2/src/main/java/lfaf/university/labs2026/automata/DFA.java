package lfaf.university.labs2026.automata;

import lfaf.university.labs2026.helpers.Production;
import lfaf.university.labs2026.helpers.State;
import lfaf.university.labs2026.grammars.Grammar;

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
        Map<State, String> stateToLetter = new HashMap<>();
        char letter = 'A';
        for (State s : states) {
            stateToLetter.put(s, String.valueOf(letter++));
        }

        Set<String> VN = new HashSet<>(stateToLetter.values());
        Set<Character> VT = alphabet;
        String S = stateToLetter.get(startState);
        List<Production> P = new ArrayList<>();

        for (Map.Entry<State, Map<Character, State>> entry : transitions.entrySet()) {
            String from = stateToLetter.get(entry.getKey());
            for (Map.Entry<Character, State> t : entry.getValue().entrySet()) {
                char terminal = t.getKey();
                String to = stateToLetter.get(t.getValue());

                if (acceptStates.contains(t.getValue())) {
                    P.add(new Production(from, String.valueOf(terminal)));
                } else {
                    P.add(new Production(from, terminal + to));
                }
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
