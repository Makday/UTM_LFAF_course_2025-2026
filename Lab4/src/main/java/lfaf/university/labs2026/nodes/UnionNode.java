package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class UnionNode extends RegexNode {
    private final RegexNode left;
    private final RegexNode right;

    public UnionNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();
        result.addAll(left.generate());
        result.addAll(right.generate());
        return result;
    }

    @Override
    public String toString() {
        return "Union(" + left + ", " + right + ")";
    }
}