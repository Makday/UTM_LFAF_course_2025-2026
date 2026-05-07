package org.example.grammars;

import org.example.helpers.Production;

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

    private List<String> sortedSet(Set<String> set) {
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    /**
     * Helper to sort productions for consistent output.
     */
    private List<Production> sortProductions(Set<Production> productions) {
        List<Production> list = new ArrayList<>(productions);
        list.sort((p1, p2) -> {
            int cmp = p1.getLhs().compareTo(p2.getLhs());
            if (cmp != 0) return cmp;
            return p1.toString().compareTo(p2.toString());
        });
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nGrammar:");
        sb.append("Start Symbol: ").append(getStartSymbol());
        sb.append("Non-Terminals: ").append(sortedSet(getNonTerminals()));
        sb.append("Terminals: ").append(sortedSet(getTerminals()));
        sb.append("Productions:");
        for (Production p : sortProductions(getProductions())) {
            sb.append("  ").append(p);
        }
        return sb.toString();
    }
}

