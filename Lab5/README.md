# Formal Languages & Finite Automata - Laboratory Works

This repository contains laboratory implementations for the **Formal Languages & Finite Automata** course.

## Laboratory 5: Chomsky Normal Form Converter

### Overview
A complete implementation of a context-free grammar converter that transforms any grammar into **Chomsky Normal Form (CNF)**.

### Quick Start

```bash
# Build the project
mvn clean package

# Run the converter with test cases
java -cp target/Lab5-1.0-SNAPSHOT.jar org.example.Main
```

### What is Chomsky Normal Form?

Chomsky Normal Form is a standardized way to write context-free grammars where every production rule has exactly one of these forms:
- **A → BC** (produces exactly two non-terminals)
- **A → a** (produces exactly one terminal)

This restriction makes grammars easier to parse algorithmically while maintaining the ability to recognize the same language.

### Key Features

✅ **Converts Any Grammar** - Works with arbitrary context-free grammars  
✅ **Multi-Step Process** - Shows all transformation stages  
✅ **Comprehensive Testing** - 7 test cases covering various scenarios  
✅ **Verified Results** - Automatic CNF compliance checking  
✅ **Well-Documented** - Clear code and detailed report  

### Implementation Stages

The converter applies transformations in this order:

1. **Epsilon Elimination** - Remove ε productions
2. **Unit Production Elimination** - Remove A → B rules  
3. **Useless Symbol Removal** - Remove unreachable/non-productive symbols
4. **CNF Transformation** - Convert to binary form and handle terminals

### Architecture

```
Grammar (input)
    ↓
EpsilonEliminator
    ↓
UnitProductionEliminator
    ↓
UselessSymbolRemover
    ↓
CNFTransformer
    ↓
Grammar (in CNF)
```

### Test Cases Included

1. **Epsilon Productions** - S → aS | bS | a | b | ε
2. **Unit Productions** - S → A | b, A → B | a, B → c
3. **Balanced Parentheses** - S → (S) | SS | ε
4. **Arithmetic Expressions** - E → E+T | T, T → T*F | F, etc.
5. **Useless Symbols** - Grammars with unreachable/non-productive symbols
6. **a^n b^n** - S → aSb | ab
7. **Complex Grammar** - Multiple symbols with epsilon and complex structure

### Project Structure

```
Lab5/
├── src/main/java/org/example/
│   ├── Main.java                      # Test harness
│   ├── Production.java                # Grammar rule
│   ├── Grammar.java                   # Grammar container
│   ├── GrammarAnalyzer.java          # Analysis utilities
│   ├── EpsilonEliminator.java        # ε elimination
│   ├── UnitProductionEliminator.java # Unit elimination
│   ├── UselessSymbolRemover.java     # Useless symbol removal
│   ├── CNFTransformer.java           # CNF transformation
│   ├── CNFConverter.java             # Main orchestrator
│   ├── GrammarExamples.java          # Test cases
│   └── IO.java                       # I/O utility
├── pom.xml                            # Maven configuration
├── REPORT.md                          # Detailed report
└── README.md                          # This file
```

### Example Usage

```java
// Create a grammar
Set<String> terminals = new HashSet<>(Arrays.asList("a", "b"));
Set<String> nonTerminals = new HashSet<>(Arrays.asList("S"));
Set<Production> productions = new HashSet<>();
productions.add(new Production("S", "a", "S"));
productions.add(new Production("S", "b"));

Grammar grammar = new Grammar("S", terminals, nonTerminals, productions);

// Convert to CNF
Grammar cnf = CNFConverter.convertToCNF(grammar);

// Result is in Chomsky Normal Form!
```

### Output Example

```
==================================================
Example 1: Grammar with Epsilon Productions
==================================================

Original grammar:
S -> 
S -> a
S -> a S
S -> b
S -> b S

Step 1: Eliminating epsilon productions...
[Grammar after epsilon elimination]

Step 2: Eliminating unit productions...
[Grammar after unit elimination]

Step 3: Removing useless symbols...
[Grammar after useless symbol removal]

Step 4: Transforming to CNF form...
Final CNF Grammar:
S0 -> X0 S | a | b
S -> X1 S | X0 S | a | b
X0 -> b
X1 -> a

✓ All productions are in CNF form!
```

### Verification

The converter includes automatic verification:
- Checks that all productions are either A → BC or A → a
- Reports any violations found
- Validates CNF compliance for each test case

### Algorithm Complexity

- **Time**: O(n³) where n = number of non-terminals (typical grammar)
- **Space**: O(n²) for storing productions and intermediate results
- Reasonable performance for practical grammars

### Technologies Used

- **Language**: Java 21
- **Build Tool**: Maven 3.6+
- **No External Dependencies**: Pure Java implementation

### Extending the Implementation

To add your own grammar:

```java
Grammar myGrammar = new Grammar(
    "StartSymbol",
    terminals,        // Set<String>
    nonTerminals,     // Set<String>
    productions       // Set<Production>
);

Grammar cnf = CNFConverter.convertToCNF(myGrammar);
CNFConverter.printGrammar(cnf);
```

### Bonus Features

✅ **Generic Solution** - Accepts any context-free grammar  
✅ **Transparent Process** - Shows all intermediate steps  
✅ **Comprehensive Testing** - 7 diverse test cases  
✅ **Modular Design** - Each component independently testable  

### Educational Value

This implementation demonstrates:
- Context-free grammar transformations
- Algorithm design and implementation
- Software architecture principles
- Formal language theory application
- Comprehensive testing practices

### References

- [Chomsky Normal Form - Wikipedia](https://en.wikipedia.org/wiki/Chomsky_normal_form)
- "Introduction to Formal Languages and Automata" by Peter Linz
- "Compilers: Principles, Techniques, and Tools" by Aho, Lam, Sethi, Ullman

### Report

For detailed information about the implementation, algorithm details, and test results, see [REPORT.md](REPORT.md).

---

**Course**: Formal Languages & Finite Automata  
**Laboratory**: 5 - Chomsky Normal Form  
**Date**: May 5, 2026

