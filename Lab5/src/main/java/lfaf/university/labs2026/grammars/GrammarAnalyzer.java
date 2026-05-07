package org.example.grammars;

import org.example.helpers.Production;

import java.util.*;

/**
 * Provides analysis utilities for context-free grammars.
 * Identifies nullable symbols, reachable symbols, productive symbols, and unit production chains.
 */
public class GrammarAnalyzer {

    /**
     * Finds all nullable non-terminals (symbols that can derive epsilon).
     */
    public static Set<String> findNullableSymbols(Grammar grammar) {
        Set<String> nullable = new HashSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Production p : grammar.getProductions()) {
                if (!nullable.contains(p.getLhs())) {
                    // Check if all RHS symbols are nullable
                    if (isNullable(p, nullable)) {
                        nullable.add(p.getLhs());
                        changed = true;
                    }
                }
            }
        }

        return nullable;
    }

    /**
     * Check if a production can derive epsilon (all RHS symbols are nullable).
     */
    private static boolean isNullable(Production p, Set<String> nullableSymbols) {
        if (p.rhsLength() == 0) {
            return true; // Epsilon production
        }
        for (int i = 0; i < p.rhsLength(); i++) {
            String symbol = p.getRhsSymbol(i);
            if (!nullableSymbols.contains(symbol)) {
                return false;
            }
        }
        return true;
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

    /**
     * Finds direct unit productions (A -> B where B is a single non-terminal).
     */
    public static Set<Production> findDirectUnitProductions(Grammar grammar) {
        Set<Production> unitProductions = new HashSet<>();
        for (Production p : grammar.getProductions()) {
            if (p.rhsLength() == 1 && grammar.isNonTerminal(p.getRhsSymbol(0))) {
                unitProductions.add(p);
            }
        }
        return unitProductions;
    }

    /**
     * Finds the transitive closure of unit productions.
     * Returns all pairs (A, B) such that A =>* B through unit productions.
     */
    public static Map<String, Set<String>> findUnitProductionClosure(Grammar grammar) {
        Map<String, Set<String>> closure = new HashMap<>();

        // Initialize with direct unit productions
        for (String nt : grammar.getNonTerminals()) {
            closure.put(nt, new HashSet<>());
        }

        for (Production p : findDirectUnitProductions(grammar)) {
            closure.get(p.getLhs()).add(p.getRhsSymbol(0));
        }

        // Compute transitive closure using Floyd-Warshall style algorithm
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String a : grammar.getNonTerminals()) {
                for (String b : new HashSet<>(closure.get(a))) {
                    for (String c : closure.get(b)) {
                        if (!closure.get(a).contains(c)) {
                            closure.get(a).add(c);
                            changed = true;
                        }
                    }
                }
            }
        }

        return closure;
    }
}

