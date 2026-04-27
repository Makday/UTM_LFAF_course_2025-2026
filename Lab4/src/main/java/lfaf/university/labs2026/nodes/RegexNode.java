package lfaf.university.labs2026.nodes;

import java.util.Set;

public abstract class RegexNode {
    public static final int maxQuantity = 5;

    public abstract Set<String> generate();

    @Override
    public abstract String toString();

    protected int limit() {
        return maxQuantity;
    }
}
