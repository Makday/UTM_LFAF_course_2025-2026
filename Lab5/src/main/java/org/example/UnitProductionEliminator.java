package org.example;

import java.util.*;

/**
 * Eliminates unit productions (A -> B where B is a non-terminal) from a grammar.
 * Step 2 in CNF conversion.
 */
public class UnitProductionEliminator {

    /**
     * Eliminates unit productions from a grammar.
     * Returns a new grammar without unit productions.
     */
    public static Grammar eliminate(Grammar grammar) {
        // Find transitive closure of unit productions
        Map<String, Set<String>> unitClosure = GrammarAnalyzer.findUnitProductionClosure(grammar);

        Set<Production> newProductions = new HashSet<>();

        // For each non-terminal and each non-terminal it can derive through unit productions
        for (String a : grammar.getNonTerminals()) {
            for (String b : unitClosure.get(a)) {
                // For each production B -> w (where w is not a single non-terminal)
                for (Production p : grammar.getProductionsFor(b)) {
                    if (p.rhsLength() != 1 || !grammar.isNonTerminal(p.getRhsSymbol(0))) {
                        // Add production A -> w
                        newProductions.add(new Production(a, p.getRhs()));
                    }
                }
            }
        }

        // Also add non-unit productions from the original grammar
        for (Production p : grammar.getProductions()) {
            if (p.rhsLength() != 1 || !grammar.isNonTerminal(p.getRhsSymbol(0))) {
                newProductions.add(p);
            }
        }

        return new Grammar(grammar.getStartSymbol(), grammar.getTerminals(), 
                          grammar.getNonTerminals(), newProductions);
    }
}

