# 4_regular_expressions

### Course: Formal Languages & Finite Automata
### Author: Cristian Bruma

----

## Theory:

Regular expressions (regex) are a formal way to describe sets of strings using symbols and operators such as concatenation, alternation (`|`), optional (`?`), repetition (`*`, `+`), fixed quantity (`{n}`) and more.

They are widely used for text validation (emails, IDs, passwords), pattern search/extraction in documents and logs, and lexical analysis in compilers/interpreters.

In this laboratory work, regex is interpreted dynamically: the input expression is tokenized, parsed into an AST, and then used to generate all valid words (with a repetition cap for unbounded operators).

## Objectives:

1. Write and cover what regular expressions are, what they are used for;

2. Below you will find 3 complex regular expressions per each variant. Take a variant depending on your number in the list of students and do the following:

   a. Write a code that will generate valid combinations of symbols conform given regular expressions (examples will be shown). Be careful that idea is to interpret the given regular expressions dinamycally, not to hardcode the way it will generate valid strings. You give a set of regexes as input and get valid word as an output

   b. In case you have an example, where symbol may be written undefined number of times, take a limit of 5 times (to evade generation of extremely long combinations);

   c. **Bonus point**: write a function that will show sequence of processing regular expression (like, what you do first, second and so on)

Write a good report covering all performed actions and faced difficulties.

## Implementation Description

The project follows a classic pipeline:

1. `Lexer` converts the input regex string into a list of tokens.
2. `Parser` applies precedence rules and builds an AST.
3. AST nodes generate valid strings according to regex semantics.
4. `RegexGenerator` orchestrates these steps.

### 1) Lexical analysis (`Lexer`)

The lexer scans the input character by character and maps symbols to token types. It also parses fixed quantifiers like `{3}` and stores the number as token metadata.

```java
public List<Token> tokenize(String input) {
    List<Token> tokens = new ArrayList<>();
    int i = 0;

    while (i < input.length()) {
        char c = input.charAt(i);
        switch (c) {
            case '(' -> { tokens.add(new Token(TokenType.LPAREN)); i++; }
            case ')' -> { tokens.add(new Token(TokenType.RPAREN)); i++; }
            case '|' -> { tokens.add(new Token(TokenType.PIPE)); i++; }
            case '+' -> { tokens.add(new Token(TokenType.PLUS)); i++; }
            case '*' -> { tokens.add(new Token(TokenType.STAR)); i++; }
            case '?' -> { tokens.add(new Token(TokenType.QUESTION)); i++; }
            case '{' -> {
                i++;
                StringBuilder number = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    number.append(input.charAt(i));
                    i++;
                }
                if (i >= input.length() || input.charAt(i) != '}') {
                    throw new IllegalArgumentException("Unclosed quantifier: missing '}'");
                }
                i++;
                tokens.add(new Token(TokenType.QUANTITY, number.toString()));
            }
            default -> { tokens.add(new Token(TokenType.CHAR, String.valueOf(c))); i++; }
        }
    }
    return tokens;
}
```

### 2) Parsing with precedence (`Parser`)

The parser is implemented as a recursive descent parser using the grammar:

- `union := concat ('|' concat)*`
- `concat := repeat+`
- `repeat := atom ('*' | '+' | '?' | QUANTITY)?`
- `atom := CHAR | '(' union ')'`

This guarantees correct operator precedence and explicit error reporting for invalid expressions.

```java
private RegexNode parseUnion() {
    RegexNode left = parseConcat();
    while (match(TokenType.PIPE)) {
        RegexNode right = parseConcat();
        left = new UnionNode(left, right);
    }
    return left;
}

private RegexNode parseRepeat() {
    RegexNode node = parseAtom();
    if (match(TokenType.STAR)) return new StarNode(node);
    if (match(TokenType.PLUS)) return new PlusNode(node);
    if (match(TokenType.QUESTION)) return new QuestionNode(node);
    if (check(TokenType.QUANTITY)) {
        int count = Integer.parseInt(advance().getMeta());
        return new QuantityNode(node, count);
    }
    return node;
}
```

### 3) AST-based generation

Each node implements `generate()` and returns a `Set<String>` of all valid strings for that subtree:

- `CharNode` -> singleton set with one symbol;
- `ConcatNode` -> Cartesian product + string concatenation;
- `UnionNode` -> set union;
- `QuestionNode` -> `""` + base set;
- `QuantityNode` -> exact `n` repetitions;
- `StarNode`/`PlusNode` -> bounded repetitions.

The repetition cap required by the task is implemented in `RegexNode`:

```java
public abstract class RegexNode {
    public static final int maxQuantity = 5;

    public abstract Set<String> generate();

    protected int limit() {
        return maxQuantity;
    }
}
```

### 4) End-to-end generation (`RegexGenerator`)

```java
public Set<String> generate(String input) {
    Lexer lexer = new Lexer();
    List<Token> tokens = lexer.tokenize(input);
    Parser parser = new Parser(tokens);
    RegexNode ast = parser.parse();
    return ast.generate();
}
```

This design keeps the solution dynamic and extensible: new operators can be added by extending tokenization, parsing rules, and AST node types.

## Conclusions

This laboratory work shows that regex generation can be implemented cleanly with a compiler-like architecture (`Lexer -> Parser -> AST`).

The final program interprets expressions dynamically (without hardcoding specific patterns), supports grouping, alternation, concatenation, and all required quantifiers (`?`, `*`, `+`, `{n}`).

A key engineering decision was limiting unbounded repetitions to 5, which prevents combinatorial explosion while preserving the intended behavior.

Main difficulties were handling precedence/associativity correctly and validating malformed input (missing `)`, invalid `{n}`, empty groups). After separating responsibilities across classes, the implementation became easier to test and maintain.

## References

1. Lattner C., Tryzelaar E. "Kaleidoscope: Kaleidoscope Introduction and the Lexer". https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html

2. Wikipedia, "Lexical Analysis". https://en.wikipedia.org/wiki/Lexical_analysis