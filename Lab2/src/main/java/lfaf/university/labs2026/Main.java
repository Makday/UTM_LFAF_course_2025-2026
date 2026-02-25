package lfaf.university.labs2026;

import lfaf.university.labs2026.automata.FiniteAutomaton;
import lfaf.university.labs2026.automata.NFA;
import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.State;

import java.util.*;

//        Variant 6
//        Q = {q0,q1,q2,q3,q4},
//        ∑ = {a,b},
//        F = {q4},
//        δ(q0,a) = q1,
//        δ(q1,b) = q1,
//        δ(q1,b) = q2,
//        δ(q2,b) = q3,
//        δ(q3,a) = q1,
//        δ(q2,a) = q4.

public class Main {
    static void main() {
        State q0 = new State("A");
        State q1 = new State("B");
        State q2 = new State("C");
        State q3 = new State("D");
        State q4 = new State("E");

        Map<State, Map<Character, Set<State>>> delta = new HashMap<>();
        delta.put(q0, new HashMap<>());
        delta.get(q0).put('a', new HashSet<>(Set.of(q1)));
        delta.put(q1, new HashMap<>());
        delta.get(q1).put('b', new HashSet<>(Set.of(q1, q2)));
        delta.put(q2, new HashMap<>());
        delta.get(q2).put('b', new HashSet<>(Set.of(q3)));
        delta.get(q2).put('a', new HashSet<>(Set.of(q4)));
        delta.put(q3, new HashMap<>());
        delta.get(q3).put('a', new HashSet<>(Set.of(q1)));

        FiniteAutomaton nfa = new NFA(
                new HashSet<>(Set.of(q0, q1, q2, q3, q4)),
                new HashSet<>(Set.of('a', 'b')),
                q0,
                new HashSet<>(Set.of(q4)),
                delta
        );

        FiniteAutomaton dfa = nfa.toDFA();
        Grammar grammar = nfa.toRegularGrammar();

        new FiniteAutomatonVisualizer(nfa, "NFA").display();
        new FiniteAutomatonVisualizer(nfa, "DFA").display();

        List<String> words = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            words.add(grammar.generateString());
        }

        System.out.println("Grammar has type : " + grammar.getGrammarType());
        System.out.println("Non-deterministic finite automaton is Deterministic : " + nfa.isDeterministic());
        System.out.println("Deterministic finite automaton is Deterministic : " + dfa.isDeterministic());
        System.out.println();

        for (String word : words) {
            System.out.printf("%s -- Accepted by non-deterministic automaton: %b\n", word, nfa.accepts(word));
            System.out.printf("%s -- Accepted by deterministic automaton: %b\n\n", word, dfa.accepts(word));
        }
    }
}
