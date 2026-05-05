package org.example;

import java.util.*;

/**
 * Represents a context-free grammar (CFG).
 * Contains terminals, non-terminals, productions, and the start symbol.
 */
public class Grammar {
    private final String startSymbol;
    private final Set<String> terminals;
    private final Set<String> nonTerminals;
    private final Set<Production> productions;

    public Grammar(String startSymbol, Set<String> terminals, Set<String> nonTerminals, Set<Production> productions) {
        this.startSymbol = startSymbol;
        this.terminals = new HashSet<>(terminals);
        this.nonTerminals = new HashSet<>(nonTerminals);
        this.productions = new HashSet<>(productions);
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public Set<String> getTerminals() {
        return new HashSet<>(terminals);
    }

    public Set<String> getNonTerminals() {
        return new HashSet<>(nonTerminals);
    }

    public Set<Production> getProductions() {
        return new HashSet<>(productions);
    }

    /**
     * Creates a copy of this grammar with potentially different components.
     */
    public Grammar copy() {
        return new Grammar(startSymbol, terminals, nonTerminals, productions);
    }

    /**
     * Check if a symbol is a terminal.
     */
    public boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    /**
     * Check if a symbol is a non-terminal.
     */
    public boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    /**
     * Get all productions for a specific non-terminal.
     */
    public Set<Production> getProductionsFor(String nonTerminal) {
        Set<Production> result = new HashSet<>();
        for (Production p : productions) {
            if (p.getLhs().equals(nonTerminal)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grammar{\n");
        sb.append("  Start Symbol: ").append(startSymbol).append("\n");
        sb.append("  Terminals: ").append(terminals).append("\n");
        sb.append("  Non-Terminals: ").append(nonTerminals).append("\n");
        sb.append("  Productions:\n");
        for (Production p : productions) {
            sb.append("    ").append(p).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}

