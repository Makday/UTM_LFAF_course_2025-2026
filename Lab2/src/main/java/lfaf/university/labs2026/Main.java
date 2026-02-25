package lfaf.university.labs2026;

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
        State q0 = new State("q0");
        State q1 = new State("q1");
        State q2 = new State("q2");
        State q3 = new State("q3");
        State q4 = new State("q4");

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

        List<String> words = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            words.add(grammar.generateString());
        }

        System.out.println("Grammar has type : " + grammar.getGrammarType());
        System.out.println("Finite Automaton is Deterministic : " + nfa.isDeterministic());

        for (String word : words) {
            System.out.printf("%s -- Accepted by non-deterministic automaton: %b\n", word, nfa.accepts(word));
            System.out.printf("%s -- Accepted by deterministic automaton: %b\n\n", word, dfa.accepts(word));
        }
    }
}
