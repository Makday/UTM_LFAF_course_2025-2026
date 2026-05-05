package org.example;

import java.util.*;

/**
 * Main CNF Converter - orchestrates all conversion steps.
 * Converts any context-free grammar to Chomsky Normal Form.
 */
public class CNFConverter {

    /**
     * Converts a grammar to Chomsky Normal Form.
     * The conversion process follows these steps:
     * 1. Eliminate epsilon productions
     * 2. Eliminate unit productions
     * 3. Remove useless symbols
     * 4. Transform to CNF (handle terminals and binary form)
     */
    public static Grammar convertToCNF(Grammar grammar) {
        IO.println("=== Converting to Chomsky Normal Form ===");
        IO.println("Original grammar:");
        printGrammar(grammar);

        // Step 1: Eliminate epsilon productions
        IO.println("\nStep 1: Eliminating epsilon productions...");
        Grammar afterEpsilon = EpsilonEliminator.eliminate(grammar);
        printGrammar(afterEpsilon);

        // Step 2: Eliminate unit productions
        IO.println("\nStep 2: Eliminating unit productions...");
        Grammar afterUnit = UnitProductionEliminator.eliminate(afterEpsilon);
        printGrammar(afterUnit);

        // Step 3: Remove useless symbols
        IO.println("\nStep 3: Removing useless symbols...");
        Grammar afterUseless = UselessSymbolRemover.remove(afterUnit);
        printGrammar(afterUseless);

        // Step 4: Transform to CNF
        IO.println("\nStep 4: Transforming to CNF form...");
        Grammar cnfGrammar = CNFTransformer.transformToCNF(afterUseless);
        IO.println("Final CNF Grammar:");
        printGrammar(cnfGrammar);

        return cnfGrammar;
    }

    /**
     * Utility method to print a grammar in a readable format.
     */
    public static void printGrammar(Grammar grammar) {
        IO.println("\nGrammar:");
        IO.println("Start Symbol: " + grammar.getStartSymbol());
        IO.println("Non-Terminals: " + sortedSet(grammar.getNonTerminals()));
        IO.println("Terminals: " + sortedSet(grammar.getTerminals()));
        IO.println("Productions:");
        for (Production p : sortProductions(grammar.getProductions())) {
            IO.println("  " + p);
        }
    }

    /**
     * Helper to convert a set to a sorted list for printing.
     */
    private static List<String> sortedSet(Set<String> set) {
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    /**
     * Helper to sort productions for consistent output.
     */
    private static List<Production> sortProductions(Set<Production> productions) {
        List<Production> list = new ArrayList<>(productions);
        list.sort((p1, p2) -> {
            int cmp = p1.getLhs().compareTo(p2.getLhs());
            if (cmp != 0) return cmp;
            return p1.toString().compareTo(p2.toString());
        });
        return list;
    }
}

