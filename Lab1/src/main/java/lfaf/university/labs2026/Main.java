package lfaf.university.labs2026;
import java.util.*;

public class Main {
    static void main() {
        Grammar grammar = new Grammar();
        NFA finiteAutomaton = grammar.toFiniteAutomaton();
        List <String> words = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            words.add(grammar.generateString());
        }

        for(String word : words) {
            System.out.printf("%s -- Accepted by automaton: %b\n", word, finiteAutomaton.stringBelongToLanguage(word));
        }
    }
}
