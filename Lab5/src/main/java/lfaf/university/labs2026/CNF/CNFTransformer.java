package org.example.CNF;

import org.example.grammars.Grammar;
import org.example.helpers.Production;

import java.util.*;

/**
 * Handles the final transformations to convert a grammar to Chomsky Normal Form.
 * This includes:
 * 1. Replacing terminals in productions with 2+ symbols with new non-terminals
 * 2. Converting productions with 3+ symbols into binary form
 * Step 4 in CNF conversion.
 */
public class CNFTransformer {

    private static int newVarCounter = 0;

    /**
     * Transforms a grammar to CNF after epsilon elimination, unit elimination, and useless symbol removal.
     * Returns a grammar in Chomsky Normal Form (all productions are either A -> BC or A -> a).
     */
    public static Grammar transformToCNF(Grammar grammar) {
        newVarCounter = 0;
        
        Set<Production> productions = new HashSet<>(grammar.getProductions());
        Set<String> nonTerminals = new HashSet<>(grammar.getNonTerminals());
        Set<String> terminals = new HashSet<>(grammar.getTerminals());

        // Step 1: Replace terminals in productions with 2+ symbols
        Set<Production> afterTerminalReplacement = new HashSet<>();
        Map<String, String> terminalToVariable = new HashMap<>();
        Set<Production> newProductionsToAdd = new HashSet<>();

        for (Production p : productions) {
            if (p.rhsLength() <= 1) {
                // Epsilon and single-symbol productions stay as is (or are already filtered)
                afterTerminalReplacement.add(p);
            } else {
                // Production with 2+ symbols - need to replace terminals
                List<String> newRhs = new ArrayList<>();
                boolean hasTerminal = false;

                for (int i = 0; i < p.rhsLength(); i++) {
                    String symbol = p.getRhsSymbol(i);
                    if (terminals.contains(symbol)) {
                        hasTerminal = true;
                        if (!terminalToVariable.containsKey(symbol)) {
                            String var = generateNewVariable(nonTerminals);
                            nonTerminals.add(var);
                            terminalToVariable.put(symbol, var);
                            newProductionsToAdd.add(new Production(var, symbol));
                        }
                        newRhs.add(terminalToVariable.get(symbol));
                    } else {
                        newRhs.add(symbol);
                    }
                }

                if (hasTerminal) {
                    afterTerminalReplacement.add(new Production(p.getLhs(), newRhs));
                } else {
                    afterTerminalReplacement.add(p);
                }
            }
        }

        afterTerminalReplacement.addAll(newProductionsToAdd);

        afterTerminalReplacement.addAll(newProductionsToAdd);

        productions = afterTerminalReplacement;

        // Step 2: Convert productions with 3+ non-terminals into binary form
        Set<Production> finalProductions = new HashSet<>();

        for (Production p : productions) {
            if (p.rhsLength() <= 2) {
                finalProductions.add(p);
            } else {
                // Convert A -> B1 B2 B3 ... Bn into binary form
                List<String> rhs = p.getRhs();
                String currentLhs = p.getLhs();

                // Create chain: A -> B1 X1, X1 -> B2 X2, X2 -> B3 X3, ..., Xn-2 -> Bn-1 Bn
                for (int i = 0; i < rhs.size() - 2; i++) {
                    String newVar = generateNewVariable(nonTerminals);
                    nonTerminals.add(newVar);
                    finalProductions.add(new Production(currentLhs, rhs.get(i), newVar));
                    currentLhs = newVar;
                }

                // Last production
                finalProductions.add(new Production(currentLhs, rhs.get(rhs.size() - 2), rhs.getLast()));
            }
        }

        return new Grammar(grammar.getStartSymbol(), terminals, nonTerminals, finalProductions);
    }

    /**
     * Generate a new unique non-terminal variable.
     */
    private static String generateNewVariable(Set<String> existingVariables) {
        String newVar;
        do {
            newVar = "X" + (newVarCounter++);
        } while (existingVariables.contains(newVar));
        return newVar;
    }
}



