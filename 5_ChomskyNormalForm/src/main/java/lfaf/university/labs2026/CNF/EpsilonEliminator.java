package lfaf.university.labs2026.CNF;

import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.Production;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Eliminates epsilon productions (A -> ε) from a grammar.
 * Step 1 in CNF conversion.
 */
public class EpsilonEliminator {

    /**
     * Eliminates epsilon productions from a grammar.
     * Returns a new grammar without epsilon productions.
     */
    public static Grammar eliminate(Grammar grammar) {
        // Find all nullable non-terminals
        Set<String> nullable = findNullableSymbols(grammar);

        // If start symbol is nullable, we need to handle it specially
        boolean startIsNullable = nullable.contains(grammar.getStartSymbol());

        Set<Production> newProductions = new HashSet<>(grammar.getProductions());
        Set<Production> addedProductions = new HashSet<>();

        // For each production, generate versions where nullable symbols are omitted
        for (Production p : grammar.getProductions()) {
            if (p.rhsLength() > 0) {
                // Find which RHS symbols are nullable
                List<Integer> nullablePositions = new ArrayList<>();
                for (int i = 0; i < p.rhsLength(); i++) {
                    if (nullable.contains(p.getRhsSymbol(i))) {
                        nullablePositions.add(i);
                    }
                }

                // Generate all combinations of omitting nullable symbols
                if (!nullablePositions.isEmpty()) {
                    for (int mask = 1; mask < (1 << nullablePositions.size()); mask++) {
                        List<String> newRhs = new ArrayList<>(p.getRhs());
                        // Remove symbols in reverse order to maintain indices
                        for (int i = nullablePositions.size() - 1; i >= 0; i--) {
                            if ((mask & (1 << i)) != 0) {
                                newRhs.remove((int) nullablePositions.get(i));
                            }
                        }
                        if (!newRhs.isEmpty()) {
                            addedProductions.add(new Production(p.getLhs(), newRhs));
                        }
                    }
                }
            }

            // Remove epsilon productions
            if (p.rhsLength() == 0) {
                newProductions.remove(p);
            }
        }

        newProductions.addAll(addedProductions);

        // If start symbol was nullable, introduce a new start symbol
        String startSymbol = grammar.getStartSymbol();
        Set<String> nonTerminals = new HashSet<>(grammar.getNonTerminals());

        if (startIsNullable) {
            String newStart = "S0";
            int counter = 0;
            while (nonTerminals.contains(newStart)) {
                newStart = "S" + (++counter);
            }
            nonTerminals.add(newStart);
            newProductions.add(new Production(newStart, startSymbol));
            // Note: We do NOT add an epsilon production for the new start symbol.
            // The grammar is now without epsilon (strict CNF requirement)
            startSymbol = newStart;
        }

        return new Grammar(startSymbol, grammar.getTerminals(), nonTerminals, newProductions);
    }

    /**
     * Finds all nullable non-terminals (symbols that can derive epsilon).
     */
    private static Set<String> findNullableSymbols(Grammar grammar) {
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
}


