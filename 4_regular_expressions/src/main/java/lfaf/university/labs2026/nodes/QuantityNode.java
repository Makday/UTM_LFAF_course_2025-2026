package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class QuantityNode extends RegexNode {
    private final RegexNode node;
    private final int count;

    public QuantityNode(RegexNode node, int count) {
        this.node = node;
        this.count = count;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();
        result.add("");

        Set<String> base = node.generate();

        for (int i = 0; i < count; i++) {
            Set<String> next = new HashSet<>();

            for (String prefix : result) {
                for (String b : base) {
                    next.add(prefix + b);
                }
            }

            result = next;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Quantity(" + node + ", " + count + ")";
    }
}