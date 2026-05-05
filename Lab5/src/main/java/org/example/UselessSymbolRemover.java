package org.example;

import java.util.*;

/**
 * Removes useless symbols from a grammar.
 * A symbol is useless if it's either not productive or not reachable.
 * Step 3 in CNF conversion.
 */
public class UselessSymbolRemover {

    /**
     * Removes useless symbols from a grammar.
     * Returns a new grammar with only useful symbols.
     */
    public static Grammar remove(Grammar grammar) {
        // First, find productive symbols
        Set<String> productive = GrammarAnalyzer.findProductiveSymbols(grammar);

        // Remove non-productive symbols from productions
        Set<Production> tempProductions = new HashSet<>();
        for (Production p : grammar.getProductions()) {
            if (!productive.contains(p.getLhs())) {
                continue; // Skip productions with non-productive LHS
            }

            boolean allProductive = true;
            for (int i = 0; i < p.rhsLength(); i++) {
                String symbol = p.getRhsSymbol(i);
                if (!grammar.isTerminal(symbol) && !productive.contains(symbol)) {
                    allProductive = false;
                    break;
                }
            }

            if (allProductive) {
                tempProductions.add(p);
            }
        }

        // Create temporary grammar with only productive symbols
        Set<String> productiveNonTerminals = new HashSet<>(grammar.getNonTerminals());
        productiveNonTerminals.retainAll(productive);

        Grammar tempGrammar = new Grammar(grammar.getStartSymbol(), grammar.getTerminals(),
                                          productiveNonTerminals, tempProductions);

        // Then, find reachable symbols
        Set<String> reachable = GrammarAnalyzer.findReachableSymbols(tempGrammar);

        // Filter to keep only reachable symbols
        Set<String> finalNonTerminals = new HashSet<>();
        for (String nt : productiveNonTerminals) {
            if (reachable.contains(nt)) {
                finalNonTerminals.add(nt);
            }
        }

        Set<Production> finalProductions = new HashSet<>();
        for (Production p : tempProductions) {
            if (finalNonTerminals.contains(p.getLhs())) {
                boolean allReachable = true;
                for (int i = 0; i < p.rhsLength(); i++) {
                    String symbol = p.getRhsSymbol(i);
                    if (!grammar.isTerminal(symbol) && !reachable.contains(symbol)) {
                        allReachable = false;
                        break;
                    }
                }
                if (allReachable) {
                    finalProductions.add(p);
                }
            }
        }

        return new Grammar(grammar.getStartSymbol(), grammar.getTerminals(),
                          finalNonTerminals, finalProductions);
    }
}

