# Chomsky Normal Form Converter - Quick Reference

## What is CNF?

Chomsky Normal Form restricts context-free grammars to only two types of productions:
1. **A → BC** - Non-terminal produces exactly two non-terminals
2. **A → a** - Non-terminal produces exactly one terminal

## Why is CNF Important?

✅ Simplifies parsing algorithms (CYK, Earley)  
✅ Facilitates theoretical proofs about CFGs  
✅ Enables efficient decision procedures  
✅ Standardizes grammar representation  

## How the Converter Works

### 4-Step Transformation

```
Input Grammar
    ↓
Step 1: Remove ε productions
    ↓
Step 2: Remove unit productions (A → B)
    ↓
Step 3: Remove useless symbols
    ↓
Step 4: Convert to binary form
    ↓
Output CNF Grammar
```

## Algorithm Overview

### Step 1: Epsilon Elimination
- Find all nullable non-terminals
- Generate alternatives without nullable symbols
- Handle start symbol specially if nullable

Example:
```
Before: S → aS | ε
After:  S → aS
        S0 → S  (new start symbol allows ε in language)
```

### Step 2: Unit Production Elimination
- Find all unit production chains (A → B → C)
- Replace with direct productions
- Remove original unit productions

Example:
```
Before: S → A, A → B, B → a
After:  S → a, A → a, B → a
```

### Step 3: Useless Symbol Removal
- Remove non-productive symbols (can't derive terminals)
- Remove unreachable symbols (not reachable from start)

Example:
```
Before: S → A, A → a, B → b, D → d  (B unreachable, D unused)
After:  S → A, A → a
```

### Step 4: CNF Transformation
- Replace terminals in multi-symbol productions
- Convert 3+ symbol productions to binary form

Example:
```
Before: A → aBC
After:  A → XaB
        B → Xb C
        Xa → a
        Xb → b
```

## Running the Converter

### Build
```bash
mvn clean package
```

### Run
```bash
java -cp target/Lab5-1.0-SNAPSHOT.jar org.example.Main
```

### Add Your Own Grammar
```java
Set<String> terminals = new HashSet<>(Arrays.asList("a", "b"));
Set<String> nonTerminals = new HashSet<>(Arrays.asList("S"));
Set<Production> productions = new HashSet<>();

// Add productions
productions.add(new Production("S", "a", "b"));
productions.add(new Production("S", "a"));

Grammar g = new Grammar("S", terminals, nonTerminals, productions);
Grammar cnf = CNFConverter.convertToCNF(g);
```

## Verification

The converter automatically checks:
- ✓ All productions are A → BC or A → a
- ✓ No epsilon productions remain
- ✓ No unit productions remain
- ✓ No useless symbols remain

## Test Cases Included

| # | Type | Example |
|---|------|---------|
| 1 | Epsilon | S → aS \| ε |
| 2 | Unit Productions | S → A, A → B, B → a |
| 3 | Complex | S → (S) \| SS \| ε |
| 4 | Recursive | E → E+T \| T, T → T*F \| F |
| 5 | Useless Symbols | Mixed productive/unreachable |
| 6 | Long Chain | S → aSb \| ab |
| 7 | Mixed Issues | Multiple ε and units |

## Key Implementation Details

### Nullable Symbol Detection
Fixed-point algorithm iterating until no new nullable symbols found
```
Repeat:
  If A → ε, mark A as nullable
  If A → X₁X₂...Xₙ and all Xᵢ nullable, mark A as nullable
Until no changes
```

### Unit Production Closure
Floyd-Warshall style transitive closure computation
```
For each non-terminal A:
  For each non-terminal B reachable through units from A:
    Add all non-unit productions of B to A
```

### Useless Symbol Removal
Two-pass algorithm
```
Pass 1: Find productive symbols (can derive terminals)
Pass 2: Find reachable symbols (reachable from start)
Keep: Symbols that are BOTH productive AND reachable
```

### Binary Conversion
Chain-based approach
```
A → B₁B₂B₃...Bₙ becomes:
A → B₁X₁
X₁ → B₂X₂
...
Xₙ₋₂ → Bₙ₋₁Bₙ
```

## Performance

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| Epsilon Elimination | O(n²) | n = number of nullable symbols |
| Unit Elimination | O(n³) | n = number of non-terminals |
| Useless Removal | O(n + m) | n = symbols, m = edges |
| CNF Transform | O(p²) | p = number of productions |

Overall: **O(n³)** for typical grammars (manageable for practical use)

## File Organization

```
src/main/java/org/example/
├── Production.java            # Rule: LHS → RHS
├── Grammar.java               # Container: S, V, T, P
├── GrammarAnalyzer.java      # Analysis: nullable, productive, reachable
├── EpsilonEliminator.java    # Remove ε
├── UnitProductionEliminator  # Remove A → B
├── UselessSymbolRemover.java # Remove useless
├── CNFTransformer.java       # Convert to CNF form
├── CNFConverter.java         # Orchestrator
├── GrammarExamples.java      # Test cases
├── Main.java                 # Test harness
└── IO.java                   # Output utility
```

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Too many new variables | Long productions | Normal - binary form requires this |
| Start symbol changed | Original start nullable | Correct - preserves ε in language |
| Some symbols removed | Unreachable/unproductive | Correct - useless removal step |
| More productions than original | Epsilon elimination | Normal - generates all combinations |

## Learning Outcomes

After studying this implementation, you understand:

1. **Formal Language Theory**
   - CFG transformation techniques
   - Normal forms for grammars
   - Formal algorithm design

2. **Algorithm Design**
   - Fixed-point algorithms
   - Graph algorithms (reachability)
   - Dynamic programming (closure)

3. **Software Engineering**
   - Modular architecture
   - Separation of concerns
   - Algorithm composition

4. **Implementation Skills**
   - Java best practices
   - Set operations and graph algorithms
   - Testing and verification

## References

- **Theory**: "Introduction to Formal Languages and Automata" - Peter Linz
- **Parsing**: "Compilers: Principles, Techniques, and Tools" - Aho et al.
- **CNF**: [Wikipedia - Chomsky Normal Form](https://en.wikipedia.org/wiki/Chomsky_normal_form)

---

**For detailed implementation details, see REPORT.md**

