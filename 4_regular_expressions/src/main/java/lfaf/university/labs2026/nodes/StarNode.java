package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class StarNode extends RegexNode {
    private final RegexNode node;

    public StarNode(RegexNode node) {
        this.node = node;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();
        result.add(""); // 0 times

        Set<String> base = node.generate();

        Set<String> current = new HashSet<>();
        current.add("");

        for (int i = 1; i <= limit(); i++) {
            Set<String> next = new HashSet<>();

            for (String prefix : current) {
                for (String b : base) {
                    next.add(prefix + b);
                }
            }

            result.addAll(next);
            current = next;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Star(" + node + ")";
    }
}