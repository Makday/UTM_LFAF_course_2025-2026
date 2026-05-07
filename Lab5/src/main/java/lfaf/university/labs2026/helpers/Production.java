package org.example.helpers;

import java.util.*;

/**
 * Represents a single production rule in a context-free grammar.
 * A production is of the form: A -> w, where A is a non-terminal and w is a sequence of symbols.
 */
public class Production {
    private final String lhs; // Left-hand side (non-terminal)
    private final List<String> rhs; // Right-hand side (sequence of terminals and non-terminals)

    public Production(String lhs, List<String> rhs) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>(rhs);
    }

    public Production(String lhs, String... symbols) {
        this.lhs = lhs;
        this.rhs = Arrays.asList(symbols);
    }

    public String getLhs() {
        return lhs;
    }

    public List<String> getRhs() {
        return new ArrayList<>(rhs);
    }

    public int rhsLength() {
        return rhs.size();
    }

    public String getRhsSymbol(int index) {
        return rhs.get(index);
    }

    @Override
    public String toString() {
        return lhs + " -> " + String.join(" ", rhs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(lhs, that.lhs) && Objects.equals(rhs, that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }
}

