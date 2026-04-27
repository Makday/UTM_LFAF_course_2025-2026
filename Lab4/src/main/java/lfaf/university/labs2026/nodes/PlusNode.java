package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class PlusNode extends RegexNode {
    private final RegexNode node;

    public PlusNode(RegexNode node) {
        this.node = node;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();

        Set<String> base = node.generate();
        Set<String> current = new HashSet<>(base);

        result.addAll(current);

        for (int i = 2; i <= limit(); i++) {
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
}