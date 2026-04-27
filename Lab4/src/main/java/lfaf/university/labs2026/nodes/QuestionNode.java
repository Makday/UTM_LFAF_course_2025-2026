package lfaf.university.labs2026.nodes;

import java.util.HashSet;
import java.util.Set;

public class QuestionNode extends RegexNode {
    private final RegexNode node;

    public QuestionNode(RegexNode node) {
        this.node = node;
    }

    @Override
    public Set<String> generate() {
        Set<String> result = new HashSet<>();
        result.add(""); // not present
        result.addAll(node.generate());
        return result;
    }
}