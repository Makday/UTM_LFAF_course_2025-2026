package lfaf.university.labs2026.grammars;

import lfaf.university.labs2026.helpers.Production;

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
        list.sort(Comparator.comparing(Production::getLhs).thenComparing(Production::toString));
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nGrammar:");
        sb.append("\nStart Symbol: ").append(getStartSymbol());
        sb.append("\nNon-Terminals: ").append(sortedSet(getNonTerminals()));
        sb.append("\nTerminals: ").append(sortedSet(getTerminals()));
        sb.append("\nProductions:\n");

        Map<String, List<String>> grouped = new LinkedHashMap<>();

        // Ensure a consistent order: start symbol first, then other non-terminals
        grouped.put(startSymbol, new ArrayList<>());
        for (String nt : nonTerminals) {
            if (!nt.equals(startSymbol)) {
                grouped.put(nt, new ArrayList<>());
            }
        }

        // Populate the map
        for (Production p : productions) {
            String lhs = p.getLhs();
            List<String> rhsSymbols = p.getRhs();
            // Convert the list of symbols to a string (space-separated, or "ε" if empty)
            String rhsStr;
            if (rhsSymbols.isEmpty()) {
                rhsStr = "ε";                     // epsilon
            } else {
                rhsStr = String.join(" ", rhsSymbols);
            }
            grouped.get(lhs).add(rhsStr);
        }

        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
            String lhs = entry.getKey();
            List<String> rhss = entry.getValue();
            if (rhss.isEmpty()) continue;   // skip non-terminals that are never used on LHS

            sb.append(lhs).append(" → ");
            sb.append(String.join(" | ", rhss));
            sb.append("\n");
        }

        // Remove the trailing newline if you prefer
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }
}

