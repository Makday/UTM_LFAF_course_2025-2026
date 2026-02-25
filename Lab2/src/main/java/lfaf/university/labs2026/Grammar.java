package lfaf.university.labs2026;
import java.util.*;

public class Grammar {
    private Set<String> VN;
    private Set<Character> VT;
    private String S;
    private List<Production> P;

    Grammar(Set<String> VN, Set<Character> VT, String S, List<Production> P) {
        this.VN = VN;
        this.VT = VT;
        this.S = S;
        this.P = P;
    }

    public String generateString() {
        StringBuilder word = new StringBuilder(S);
        Random rand = new Random();

        while (true) {
            boolean hasNonTerminal = false;
            StringBuilder next = new StringBuilder();

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                String asString = String.valueOf(c);

                if (VN.contains(asString)) {
                    hasNonTerminal = true;
                    List<String> options = new ArrayList<>();
                    for (Production p : P) {
                        if (p.getLhs().equals(asString)) options.add(p.getRhs());
                    }
                    next.append(options.get(rand.nextInt(options.size())));
                } else {
                    next.append(c);
                }
            }

            word = next;
            if (!hasNonTerminal) break;
        }

        return word.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        Set<State> Q = new HashSet<>();
        for (String nt : VN) Q.add(new State(nt));
        State acceptSink = new State("X");
        Q.add(acceptSink);

        Set<State> F = new HashSet<>();
        F.add(acceptSink);

        Map<State, Map<Character, Set<State>>> delta = new HashMap<>();

        for (Production prod : P) {
            State from = new State(prod.getLhs());
            delta.computeIfAbsent(from, k -> new HashMap<>());

            String rhs = prod.getRhs();
            if (rhs.isEmpty()) {
                F.add(from);
            } else if (rhs.length() == 1) {
                char terminal = rhs.charAt(0);
                delta.get(from)
                        .computeIfAbsent(terminal, k -> new HashSet<>())
                        .add(acceptSink);
            } else {
                char terminal = rhs.charAt(0);
                State to = new State(rhs.substring(1));
                delta.get(from)
                        .computeIfAbsent(terminal, k -> new HashSet<>())
                        .add(to);
            }
        }

        return new NFA(Q, VT, new State(S), F, delta);
    }

    public GrammarType getGrammarType() {
        if (isRegularGrammar())
            return GrammarType.TYPE_3;

        if (isContextFreeGrammar())
            return GrammarType.TYPE_2;

        if (isContextSensitiveGrammar())
            return GrammarType.TYPE_1;

        return GrammarType.TYPE_0;
    }

    private boolean isRegularGrammar() {
        boolean isLeftLinear = false;
        boolean isRightLinear = false;

        boolean sToEpsilon = false;
        boolean sOnRight = false;

        for (Production prod : P) {
            String lhs = prod.getLhs();
            String rhs = prod.getRhs();

            if (lhs.length() != 1 || !VN.contains(lhs))
                return false;

            if (rhs.isEmpty()) {
                if (!S.equals(lhs))
                    return false;
                sToEpsilon = true;
                continue;
            }

            if (rhs.contains(S))
                sOnRight = true;

            if (rhs.length() == 1) {
                char symbol = rhs.charAt(0);
                if (!VT.contains(symbol))
                    return false;
            }

            if (rhs.length() == 2) {
                char first = rhs.charAt(0);
                char second = rhs.charAt(1);

                if (VT.contains(first) && VN.contains(String.valueOf(second))) {
                    isRightLinear = true;
                } else if (VN.contains(String.valueOf(first)) && VT.contains(second)) {
                    isLeftLinear = true;
                } else {
                    return false;
                }
            }

            if (rhs.length() > 2) {
                return false;
            }
        }

        if (sToEpsilon && sOnRight) return false;
        return !(isLeftLinear && isRightLinear);
    }

    private boolean isContextFreeGrammar() {
        for (Production prod : P) {
            if (prod.getLhs().length() != 1 || !VN.contains(prod.getLhs()))
                return false;
        }
        return true;
    }

    private boolean isContextSensitiveGrammar() {
        boolean sToEpsilon = false;
        boolean sOnRight = false;

        for (Production prod : P) {
            String lhs = prod.getLhs();
            String rhs = prod.getRhs();

            if (rhs.isEmpty()) {
                if (!S.equals(lhs)) return false;
                sToEpsilon = true;
                continue;
            }

            if (rhs.contains(S)) sOnRight = true;
            if (lhs.length() > rhs.length()) return false;
        }

        return !(sToEpsilon && sOnRight);
    }
}