package lfaf.university.labs2026.CNF;

import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.Production;

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
        Map<String, Set<String>> unitClosure = UnitProductionEliminator.findUnitProductionClosure(grammar);

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

