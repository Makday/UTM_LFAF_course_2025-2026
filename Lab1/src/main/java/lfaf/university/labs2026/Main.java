package lfaf.university.labs2026;

import java.util.*;

public class Main {
    static void main() {
        Grammar grammar = new Grammar(Set.of('S', 'I', 'J', 'K'),
                Set.of('a', 'b', 'c', 'e', 'n', 'f', 'm'),
                'S',
                Map.of(
                        'S', List.of("cI"),
                        'I', List.of("bJ", "fI", "eK", "e"),
                        'J', List.of("nJ", "cS"),
                        'K', List.of("nK", "m")));
        NFA finiteAutomaton = grammar.toFiniteAutomaton();
        List<String> words = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            words.add(grammar.generateString());
        }

        for (String word : words) {
            System.out.printf("%s -- Accepted by automaton: %b\n", word, finiteAutomaton.stringBelongToLanguage(word));
        }
    }
}
