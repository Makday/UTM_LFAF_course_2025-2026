package lfaf.university.labs2026.CNF;

import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.Production;

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
        Set<String> productive = UselessSymbolRemover.findProductiveSymbols(grammar);

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
        Set<String> reachable = UselessSymbolRemover.findReachableSymbols(tempGrammar);

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


    /**
     * Finds all productive non-terminals (symbols that can derive terminal strings).
     */
    public static Set<String> findProductiveSymbols(Grammar grammar) {
        Set<String> productive = new HashSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Production p : grammar.getProductions()) {
                if (!productive.contains(p.getLhs())) {
                    // Check if all RHS symbols are terminals or productive non-terminals
                    if (isProductive(p, productive, grammar)) {
                        productive.add(p.getLhs());
                        changed = true;
                    }
                }
            }
        }

        return productive;
    }

    /**
     * Check if a production can derive a terminal string.
     */
    private static boolean isProductive(Production p, Set<String> productiveSymbols, Grammar grammar) {
        for (int i = 0; i < p.rhsLength(); i++) {
            String symbol = p.getRhsSymbol(i);
            if (!grammar.isTerminal(symbol) && !productiveSymbols.contains(symbol)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds all reachable symbols from the start symbol.
     */
    public static Set<String> findReachableSymbols(Grammar grammar) {
        Set<String> reachable = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        reachable.add(grammar.getStartSymbol());
        queue.add(grammar.getStartSymbol());

        while (!queue.isEmpty()) {
            String symbol = queue.poll();
            for (Production p : grammar.getProductionsFor(symbol)) {
                for (int i = 0; i < p.rhsLength(); i++) {
                    String rhsSymbol = p.getRhsSymbol(i);
                    if (!reachable.contains(rhsSymbol)) {
                        reachable.add(rhsSymbol);
                        if (grammar.isNonTerminal(rhsSymbol)) {
                            queue.add(rhsSymbol);
                        }
                    }
                }
            }
        }

        return reachable;
    }
}

