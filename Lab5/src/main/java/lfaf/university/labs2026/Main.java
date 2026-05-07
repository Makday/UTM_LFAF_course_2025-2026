package lfaf.university.labs2026;

import lfaf.university.labs2026.CNF.CNFConverter;
import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.Production;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class for the Chomsky Normal Form Converter.
 * Tests the CNF converter with various grammar examples.
 */
public class Main {
    public static void main(String[] args) {
        // Variant 6
        Set<String> terminals = new HashSet<>(List.of("a", "b"));
        Set<String> nonTerminals = new HashSet<>(List.of("S", "A", "B", "C", "E"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "a", "B"));
        productions.add(new Production("S", "a", "B"));
        productions.add(new Production("S", "A", "C"));
        productions.add(new Production("A", "a"));
        productions.add(new Production("A", "A", "S", "C"));
        productions.add(new Production("A", "B", "C"));
        productions.add(new Production("B", "b"));
        productions.add(new Production("B", "b", "S"));
        productions.add(new Production("C"));
        productions.add(new Production("C", "B", "A"));
        productions.add(new Production("E", "b", "B"));


        var grammar = new Grammar("S", terminals, nonTerminals, productions);

        CNFConverter.debugMode = false;
        Grammar cnf = CNFConverter.convertToCNF(grammar);
        System.out.println(grammar);
        System.out.println(cnf);

    }
}
