# 5_ChomskyNormalForm

### Course: Formal Languages & Finite Automata
### Author: Cristian Bruma

----

## Theory:

Chomsky Normal Form (CNF) is a restriction for context-free grammars where each production has one of the following forms:

- `A -> BC`, where `A`, `B`, `C` are non-terminals
- `A -> a`, where `a` is a terminal
- `S -> ε`, where `S` is the start symbol and can produce the empty string

## Objectives:

1. Learn about Chomsky Normal Form (CNF) [1].
2. Get familiar with the approaches of normalizing a grammar.
3. Implement a method for normalizing an input grammar by the rules of CNF.
    1. The implementation needs to be encapsulated in a method with an appropriate signature (also ideally in an appropriate class/type).
    2. The implemented functionality needs executed and tested.
    3. Also, another **BONUS point** would be given if the student will make the aforementioned function to accept any grammar, not only the one from the student's variant.

## Implementation Description

The solution is implemented in a modular way. The main entry point is `CNFConverter`, which applies the normalization pipeline step by step.

```java
public static Grammar convertToCNF(Grammar grammar) {
    Grammar afterEpsilon = EpsilonEliminator.eliminate(grammar);
    Grammar afterUnit = UnitProductionEliminator.eliminate(afterEpsilon);
    Grammar afterUseless = UselessSymbolRemover.remove(afterUnit);
    return CNFTransformer.transformToCNF(afterUseless);
}
```

The grammar is represented by `Grammar` and `Production` classes:

```java
public class Production {
    private final String lhs;
    private final List<String> rhs;
}
```

The final CNF rewriting is done by replacing terminals in long productions and splitting rules with more than two symbols:

```java
if (p.rhsLength() > 2) {
    // A -> B C D becomes A -> B X0, X0 -> C D
}
```

The program is executed from `Main`, which creates example grammars and verifies that the resulting grammar contains only CNF productions.



## Conclusions

This lab helped me understand how a context-free grammar can be normalized into CNF. The implementation covers the full conversion process and works for different types of grammars, not only one specific variant. The project is also tested with several examples, which confirms that the final grammar respects CNF rules.



## References

1. Wikipedia, "Chomsky Normal Form". https://en.wikipedia.org/wiki/Chomsky_normal_form
2. GeeksForGeeks, "Chomsky Normal Form". https://www.geeksforgeeks.org/theory-of-computation/converting-context-free-grammar-chomsky-normal-form/