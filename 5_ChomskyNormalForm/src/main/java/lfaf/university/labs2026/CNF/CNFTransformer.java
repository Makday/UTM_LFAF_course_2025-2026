package lfaf.university.labs2026.CNF;

import lfaf.university.labs2026.grammars.Grammar;
import lfaf.university.labs2026.helpers.Production;

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

        productions = afterTerminalReplacement;

        // Step 2: Convert productions with 3+ non-terminals into binary form
        Set<Production> finalProductions = new HashSet<>();
        Map<List<String>, String> binaryPairToVariable = new HashMap<>();

        for (Production p : productions) {
            if (p.rhsLength() <= 2) {
                addProductionIfAbsent(finalProductions, p);
            } else {
                // Convert A -> B1 B2 B3 ... Bn into binary form while reusing
                // already created variables for the same binary pair.
                List<String> rhs = p.getRhs();
                String chainedTail = createOrReuseBinaryPair(
                        rhs.get(rhs.size() - 2),
                        rhs.get(rhs.size() - 1),
                        nonTerminals,
                        finalProductions,
                        binaryPairToVariable
                );

                for (int i = rhs.size() - 3; i >= 1; i--) {
                    chainedTail = createOrReuseBinaryPair(
                            rhs.get(i),
                            chainedTail,
                            nonTerminals,
                            finalProductions,
                            binaryPairToVariable
                    );
                }

                addProductionIfAbsent(finalProductions, new Production(p.getLhs(), rhs.get(0), chainedTail));
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

    private static String createOrReuseBinaryPair(String left, String right,
                                                   Set<String> nonTerminals,
                                                   Set<Production> targetProductions,
                                                   Map<List<String>, String> binaryPairToVariable) {
        List<String> key = List.of(left, right);
        String existingVariable = binaryPairToVariable.get(key);
        if (existingVariable != null) {
            return existingVariable;
        }

        String newVar = generateNewVariable(nonTerminals);
        nonTerminals.add(newVar);
        binaryPairToVariable.put(key, newVar);
        addProductionIfAbsent(targetProductions, new Production(newVar, left, right));
        return newVar;
    }

    private static void addProductionIfAbsent(Set<Production> targetProductions, Production production) {
        targetProductions.add(production);
    }
}



