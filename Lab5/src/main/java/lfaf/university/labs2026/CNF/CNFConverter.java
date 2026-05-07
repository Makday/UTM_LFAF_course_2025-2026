package org.example.CNF;

import org.example.grammars.Grammar;

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
        System.out.println("=== Converting to Chomsky Normal Form ===");
        System.out.println("Original grammar:");
        System.out.println(grammar);

        // Step 1: Eliminate epsilon productions
        System.out.println("\nStep 1: Eliminating epsilon productions...");
        Grammar afterEpsilon = EpsilonEliminator.eliminate(grammar);
        System.out.println(afterEpsilon);

        // Step 2: Eliminate unit productions
        System.out.println("\nStep 2: Eliminating unit productions...");
        Grammar afterUnit = UnitProductionEliminator.eliminate(afterEpsilon);
        System.out.println(afterUnit);

        // Step 3: Remove useless symbols
        System.out.println("\nStep 3: Removing useless symbols...");
        Grammar afterUseless = UselessSymbolRemover.remove(afterUnit);
        System.out.println(afterUseless);

        // Step 4: Transform to CNF
        System.out.println("\nStep 4: Transforming to CNF form...");
        Grammar cnfGrammar = CNFTransformer.transformToCNF(afterUseless);
        System.out.println("Final CNF Grammar:");
        System.out.println(cnfGrammar);

        return cnfGrammar;
    }
}

