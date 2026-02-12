package lfaf.university.labs2026;
import java.util.*;

public class Grammar {
//    VN={S, I, J, K},
//    VT={a, b, c, e, n, f, m},
//    P={
//        S → cI

//        I → bJ
//        I → fI
//        I → eK
//        I → e

//        J → nJ
//        J → cS


//        K → m
//        K → nK
//    }

    private Set<Character> VN = Set.of('S', 'I', 'J', 'K');
    private Set<Character> VT = Set.of('a', 'b', 'c', 'e', 'n', 'f', 'm');
    private Character S = 'S';
    private Map<Character, List<String>> P = Map.of(
            'S', List.of("cI"),
            'I', List.of("bJ", "fI", "eK", "e"),
            'J', List.of("nJ", "cS"),
            'K', List.of("nK", "m")
    );

    public String generateString(){
        StringBuilder word = new StringBuilder(String.valueOf(S));
        Random rand = new Random();

        while(true) {
            boolean hasNonTerminal = false;
            StringBuilder next = new StringBuilder();

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (VN.contains(c)) {
                    hasNonTerminal = true;
                    List<String> p = P.get(c);
                    next.append(p.get(rand.nextInt(p.size())));
                } else {
                    next.append(c);
                }
            }

            word = next;
            if (!hasNonTerminal) break;
        }

        return word.toString();
    }

    public NFA toFiniteAutomaton(){
        Set<Character> Q = new HashSet<>(VN);
        Q.add('X');

        Set<Character> F = Set.of('X');

        Map<Character, Map<Character, Set<Character>>> delta = new HashMap<>();

        for (Character state : P.keySet()) {
            delta.put(state, new HashMap<>());

            for (String production : P.get(state)) {
                if (production.length() == 1) {
                    char terminal = production.charAt(0);
                    delta.get(state)
                            .computeIfAbsent(terminal, k -> new HashSet<>())
                            .add('X');
                } else {
                    char terminal = production.charAt(0);
                    char nextState = production.charAt(1);
                    delta.get(state)
                            .computeIfAbsent(terminal, k -> new HashSet<>())
                            .add(nextState);
                }
            }
        }

        return new NFA(Q, VT, delta, S, F);
    }
}
