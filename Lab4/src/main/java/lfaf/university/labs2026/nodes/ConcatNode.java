package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class ConcatNode extends RegexNode {
    RegexNode left, right;

    public ConcatNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();

        for (String l : left.generate()) {
            for (String r : right.generate()) {
                result.add(l + r);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "Concat(" + left + ", " + right + ")";
    }
}
