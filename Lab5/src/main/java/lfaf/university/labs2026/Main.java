package org.example;

import org.example.CNF.CNFConverter;
import org.example.grammars.Grammar;
import org.example.grammars.GrammarExamples;
import org.example.helpers.Production;

/**
 * Main class for the Chomsky Normal Form Converter.
 * Tests the CNF converter with various grammar examples.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    Chomsky Normal Form Converter");
        System.out.println("========================================\n");

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

        System.out.println("\n========================================");
        System.out.println("    Conversion Complete!");
        System.out.println("========================================");
    }

    /**
     * Test a grammar by converting it to CNF.
     */
    private static void testGrammar(String title, Grammar grammar) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(title);
        System.out.println("=".repeat(50));
        
        Grammar cnf = CNFConverter.convertToCNF(grammar);
        
        System.out.println("\n" + "-".repeat(50));
        System.out.println("Verification: CNF Grammar has only:");
        System.out.println("  - Productions A -> BC (two non-terminals)");
        System.out.println("  - Productions A -> a (single terminal)");
        System.out.println("-".repeat(50));
        
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
                System.out.println("  ✗ VIOLATION: " + p);
            }
        }

        if (isValid) {
            System.out.println("  ✓ All productions are in CNF form!");
        } else {
            System.out.println("  ✗ Found " + violationCount + " violations!");
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
