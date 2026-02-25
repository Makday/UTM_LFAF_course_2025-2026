package lfaf.university.labs2026;

import java.util.Set;

public abstract class FiniteAutomaton {
    protected Set<State> states;
    protected Set<Character> alphabet;
    protected State startState;
    protected Set<State> acceptStates;

    public FiniteAutomaton(Set<State> states, Set<Character> alphabet, State startState, Set<State> acceptStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.acceptStates = acceptStates;
    }

    public abstract boolean accepts(String input);
    public abstract Grammar toRegularGrammar();
    public abstract boolean isDeterministic();
    public abstract DFA toDFA();
}
