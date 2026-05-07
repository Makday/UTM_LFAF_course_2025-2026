package lfaf.university.labs2026.CNF;

import lfaf.university.labs2026.grammars.Grammar;

/**
 * Main CNF Converter - orchestrates all conversion steps.
 * Converts any context-free grammar to Chomsky Normal Form.
 */
public class CNFConverter {
    public static boolean debugMode = false;

    /**
     * Converts a grammar to Chomsky Normal Form.
     * The conversion process follows these steps:
     * 1. Eliminate epsilon productions
     * 2. Eliminate unit productions
     * 3. Remove useless symbols
     * 4. Transform to CNF (handle terminals and binary form)
     */
    public static Grammar convertToCNF(Grammar grammar) {
        // Step 1: Eliminate epsilon productions
        Grammar afterEpsilon = EpsilonEliminator.eliminate(grammar);
        // Step 2: Eliminate unit productions
        Grammar afterUnit = UnitProductionEliminator.eliminate(afterEpsilon);
        // Step 3: Remove useless symbols
        Grammar afterUseless = UselessSymbolRemover.remove(afterUnit);
        // Step 4: Transform to CNF
        Grammar cnfGrammar = CNFTransformer.transformToCNF(afterUseless);

        if(debugMode) {
            System.out.println("=== Converting to Chomsky Normal Form ===");
            System.out.println("Original grammar:");
            System.out.println(grammar);

            System.out.println("\nStep 1: Eliminating epsilon productions...");
            System.out.println(afterEpsilon);

            System.out.println("\nStep 2: Eliminating unit productions...");
            System.out.println(afterUnit);

            System.out.println("\nStep 3: Removing useless symbols...");
            System.out.println(afterUseless);

            System.out.println("\nStep 4: Transforming to CNF form...");
            System.out.println("Final CNF Grammar:");
            System.out.println(cnfGrammar);
        }
        return cnfGrammar;
    }
}

