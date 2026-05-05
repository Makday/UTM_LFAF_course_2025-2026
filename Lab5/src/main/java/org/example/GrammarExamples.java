package org.example;

import java.util.*;

/**
 * Provides example grammars for testing the CNF converter.
 * These are commonly used examples in formal language theory.
 */
public class GrammarExamples {

    /**
     * Example 1: Simple grammar with epsilon productions
     * S -> aS | bS | a | b | ε
     */
    public static Grammar exampleWithEpsilon() {
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "a", "S"));
        productions.add(new Production("S", "b", "S"));
        productions.add(new Production("S", "a"));
        productions.add(new Production("S", "b"));
        productions.add(new Production("S")); // epsilon

        return new Grammar("S", terminals, nonTerminals, productions);
    }

    /**
     * Example 2: Grammar with unit productions
     * S -> A | b
     * A -> B | a
     * B -> c
     */
    public static Grammar exampleWithUnitProductions() {
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "A", "B"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "A"));
        productions.add(new Production("S", "b"));
        productions.add(new Production("A", "B"));
        productions.add(new Production("A", "a"));
        productions.add(new Production("B", "c"));

        return new Grammar("S", terminals, nonTerminals, productions);
    }

    /**
     * Example 3: Balanced parentheses grammar
     * S -> (S) | SS | ε
     */
    public static Grammar balancedParentheses() {
        Set<String> terminals = new HashSet<>(Arrays.asList("(", ")"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "(", "S", ")"));
        productions.add(new Production("S", "S", "S"));
        productions.add(new Production("S")); // epsilon

        return new Grammar("S", terminals, nonTerminals, productions);
    }

    /**
     * Example 4: Simple arithmetic expression grammar
     * E -> E + T | T
     * T -> T * F | F
     * F -> (E) | id
     */
    public static Grammar arithmeticExpression() {
        Set<String> terminals = new HashSet<>(Arrays.asList("+", "*", "(", ")", "id"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("E", "T", "F"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("E", "E", "+", "T"));
        productions.add(new Production("E", "T"));
        productions.add(new Production("T", "T", "*", "F"));
        productions.add(new Production("T", "F"));
        productions.add(new Production("F", "(", "E", ")"));
        productions.add(new Production("F", "id"));

        return new Grammar("E", terminals, nonTerminals, productions);
    }

    /**
     * Example 5: Grammar with useless symbols
     * S -> A
     * A -> a | B
     * B -> b | C
     * C -> c
     * D -> d (unreachable)
     * E -> S | e (non-productive)
     */
    public static Grammar exampleWithUselessSymbols() {
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c", "d", "e"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "A", "B", "C", "D", "E"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "A"));
        productions.add(new Production("A", "a"));
        productions.add(new Production("A", "B"));
        productions.add(new Production("B", "b"));
        productions.add(new Production("B", "C"));
        productions.add(new Production("C", "c"));
        productions.add(new Production("D", "d")); // unreachable
        productions.add(new Production("E", "S")); // non-productive if S produces non-terminal not in grammar
        productions.add(new Production("E", "e")); // non-productive if E can't be reached

        return new Grammar("S", terminals, nonTerminals, productions);
    }

    /**
     * Example 6: Grammar for simple expressions a^n b^n
     * S -> aSb | ab
     */
    public static Grammar anbn() {
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "a", "S", "b"));
        productions.add(new Production("S", "a", "b"));

        return new Grammar("S", terminals, nonTerminals, productions);
    }

    /**
     * Example 7: Complex grammar with multiple symbols
     * S -> ABa | B | ε
     * A -> aab | B
     * B -> bac | ε
     */
    public static Grammar complexGrammar() {
        Set<String> terminals = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> nonTerminals = new HashSet<>(Arrays.asList("S", "A", "B"));
        Set<Production> productions = new HashSet<>();

        productions.add(new Production("S", "A", "B", "a"));
        productions.add(new Production("S", "B"));
        productions.add(new Production("S")); // epsilon
        productions.add(new Production("A", "a", "a", "b"));
        productions.add(new Production("A", "B"));
        productions.add(new Production("B", "b", "a", "c"));
        productions.add(new Production("B")); // epsilon

        return new Grammar("S", terminals, nonTerminals, productions);
    }
}

