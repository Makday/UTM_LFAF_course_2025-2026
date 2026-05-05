package org.example;

/**
 * Main class for the Chomsky Normal Form Converter.
 * Tests the CNF converter with various grammar examples.
 */
public class Main {
    public static void main(String[] args) {
        IO.println("========================================");
        IO.println("    Chomsky Normal Form Converter");
        IO.println("========================================\n");

        // Test with different example grammars
        testGrammar("Example 1: Grammar with Epsilon Productions", 
                   GrammarExamples.exampleWithEpsilon());

        testGrammar("Example 2: Grammar with Unit Productions",
                   GrammarExamples.exampleWithUnitProductions());

        testGrammar("Example 3: Balanced Parentheses",
                   GrammarExamples.balancedParentheses());

        testGrammar("Example 4: Simple Arithmetic Expressions",
                   GrammarExamples.arithmeticExpression());

        testGrammar("Example 5: Grammar with Useless Symbols",
                   GrammarExamples.exampleWithUselessSymbols());

        testGrammar("Example 6: a^n b^n Grammar",
                   GrammarExamples.anbn());

        testGrammar("Example 7: Complex Grammar",
                   GrammarExamples.complexGrammar());

        IO.println("\n========================================");
        IO.println("    Conversion Complete!");
        IO.println("========================================");
    }

    /**
     * Test a grammar by converting it to CNF.
     */
    private static void testGrammar(String title, Grammar grammar) {
        IO.println("\n" + "=".repeat(50));
        IO.println(title);
        IO.println("=".repeat(50));
        
        Grammar cnf = CNFConverter.convertToCNF(grammar);
        
        IO.println("\n" + "-".repeat(50));
        IO.println("Verification: CNF Grammar has only:");
        IO.println("  - Productions A -> BC (two non-terminals)");
        IO.println("  - Productions A -> a (single terminal)");
        IO.println("-".repeat(50));
        
        verifyCNF(cnf);
    }

    /**
     * Verify that a grammar is in CNF.
     */
    private static void verifyCNF(Grammar grammar) {
        boolean isValid = true;
        int violationCount = 0;

        for (Production p : grammar.getProductions()) {
            if (!isCNFProduction(p, grammar)) {
                isValid = false;
                violationCount++;
                IO.println("  ✗ VIOLATION: " + p);
            }
        }

        if (isValid) {
            IO.println("  ✓ All productions are in CNF form!");
        } else {
            IO.println("  ✗ Found " + violationCount + " violations!");
        }
    }

    /**
     * Check if a production is in CNF.
     * Valid forms: A -> BC (two non-terminals) or A -> a (terminal)
     */
    private static boolean isCNFProduction(Production p, Grammar grammar) {
        int rhsLen = p.rhsLength();

        // Check for epsilon (should not exist after conversion)
        if (rhsLen == 0) {
            return false;
        }

        // Single terminal: A -> a
        if (rhsLen == 1) {
            return grammar.isTerminal(p.getRhsSymbol(0));
        }

        // Two non-terminals: A -> BC
        if (rhsLen == 2) {
            return grammar.isNonTerminal(p.getRhsSymbol(0)) && 
                   grammar.isNonTerminal(p.getRhsSymbol(1));
        }

        // More than two symbols: not in CNF
        return false;
    }
}
