package lfaf.university.labs2026;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class State {
    private final String name;

    public State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        return name.equals(((State) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static State fromSet(Set<State> states) {
        String name = states.stream()
                .map(State::getName)
                .sorted()
                .collect(Collectors.joining(",", "{", "}"));
        return new State(name);
    }
}
