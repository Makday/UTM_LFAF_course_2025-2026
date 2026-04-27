package lfaf.university.labs2026.nodes;

import java.util.Set;

public class CharNode extends RegexNode {
    private final String value;

    public CharNode(String value) {
        this.value = value;
    }

    @Override
    public Set<String> generate() {
        return Set.of(value);
    }
}
