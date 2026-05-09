# 3_LexerScanner

### Course: Formal Languages & Finite Automata
### Author: Cristian Bruma

----

## Objectives:

1. Understand what lexical analysis is.
2. Get familiar with the inner workings of a lexer/scanner/tokenizer.
3. Implement a sample lexer and show how it works.

## Implementation Description

The laboratory work implements a hand-written lexer for a subset of SQL, capable of tokenizing `SELECT`, `INSERT`, `UPDATE`, and `DELETE` statements. The implementation consists of three classes: `TokenType`, `Token`, and `Lexer`.

### TokenType

`TokenType` is an enum that defines every category of token the lexer can produce. It is grouped into five logical sections: SQL keywords (`SELECT`, `FROM`, `WHERE`, `AND`, etc.), literal types (`INTEGER`, `FLOAT`, `STRING`, `BOOLEAN`), identifiers, comparison and punctuation operators, and a terminal `EOF` marker.

```java
public enum TokenType {
    SELECT, FROM, WHERE, AND, OR, NOT,
    INSERT, INTO, VALUES,
    UPDATE, SET, DELETE,
    ORDER, BY, ASC, DESC, LIMIT,
 
    INTEGER, FLOAT, STRING, BOOLEAN,
    IDENTIFIER,
 
    EQ, NEQ, LT, GT, LTE, GTE,
    COMMA, SEMICOLON, LPAREN, RPAREN, ASTERISK, DOT,
 
    EOF
}
```

### Token

`Token` is an immutable data class that pairs a `TokenType` with an optional metadata string. Keywords like `SELECT` or `FROM` carry no metadata since the type alone is sufficient information, while literals and identifiers carry their raw value.

```java
public class Token {
    private final TokenType type;
    private final String meta;
 
    public Token(TokenType type, String meta) { ... }
    public Token(TokenType type) { this(type, null); }
 
    @Override
    public String toString() {
        return meta != null
            ? "Token(" + type + ", \"" + meta + "\")"
            : "Token(" + type + ")";
    }
}
```

### Lexer

The `Lexer` class is the core of the implementation. It takes a raw SQL string as input and produces a flat list of tokens via its `tokenize()` method.

#### Sentinel character

A null sentinel character `'\0'` is appended to the input string in the constructor:

```java
public Lexer(String input) {
    this.input = input + '\0';
    this.pos = 0;
}
```

This technique, borrowed from the Clang compiler's lexer, eliminates the need for bounds checks inside every helper method. Since `'\0'` fails every character classification (`isDigit`, `isLetter`, `isWhitespace`), all inner loops terminate naturally when the cursor reaches the end of input. The only place where `'\0'` is checked explicitly is inside `readString()`, to detect and report unterminated string literals.

#### Main dispatch loop

The `tokenize()` method drives the lexer with a simple loop that peeks at the first character of the next token and dispatches to the appropriate reader:

```java
while (current() != '\0') {
    skipWhitespace();
    char c = current();
 
    if (Character.isLetter(c) || c == '_')  tokens.add(readWord());
    else if (Character.isDigit(c))           tokens.add(readNumber());
    else if (c == '\'')                      tokens.add(readString());
    else                                     tokens.add(readSymbol());
}
tokens.add(new Token(TokenType.EOF));
```

#### readWord() — Keywords and Identifiers

`readWord()` consumes characters as long as they are alphanumeric or underscores, then compares the extracted word — uppercased — against the keyword set:

```java
private Token readWord() {
    int start = pos;
    while (Character.isLetterOrDigit(current()) || current() == '_') pos++;
    String word = input.substring(start, pos);
    String upper = word.toUpperCase();
 
    if (KEYWORDS.contains(upper)) {
        if (upper.equals("TRUE") || upper.equals("FALSE"))
            return new Token(TokenType.BOOLEAN, upper);
        return new Token(TokenType.valueOf(upper));
    }
    return new Token(TokenType.IDENTIFIER, word);
}
```

The keyword set is a `static final Set<String>`, making membership checks O(1). `TokenType.valueOf(upper)` converts a string like `"SELECT"` directly into its enum constant — this works because the enum names deliberately mirror the SQL keyword strings. Boolean literals `TRUE` and `FALSE` are special-cased as they carry a value unlike structural keywords.

#### readNumber() — Integers and Floats

`readNumber()` first consumes the integer part, then checks whether a decimal point followed by another digit is present. This two-character lookahead (`current() == '.'` and `peek()` is a digit) prevents misreading a `table.column` dot as a decimal point:

```java
private Token readNumber() {
    int start = pos;
    boolean isFloat = false;
 
    while (Character.isDigit(current())) pos++;
 
    if (current() == '.' && Character.isDigit(peek())) {
        isFloat = true;
        pos++;
        while (Character.isDigit(current())) pos++;
    }
 
    String value = input.substring(start, pos);
    return new Token(isFloat ? TokenType.FLOAT : TokenType.INTEGER, value);
}
```

#### readString() — String Literals

`readString()` skips the opening single quote, then scans forward until it finds the closing quote or the sentinel. An unterminated string throws a descriptive runtime exception:

```java
private Token readString() {
    pos++;
    int start = pos;
    while (current() != '\'' && current() != '\0') pos++;
    if (current() == '\0')
        throw new RuntimeException("Unterminated string at position " + start);
    String value = input.substring(start, pos);
    pos++; // skip closing '
    return new Token(TokenType.STRING, value);
}
```

#### readSymbol() — Operators and Punctuation

`readSymbol()` handles both single-character symbols and two-character operators. It consumes the first character, then immediately peeks at the next to check for `!=`, `<=`, and `>=` — without any bounds check, since the sentinel guarantees a safe read:

```java
private Token readSymbol() {
    char c = current();
    pos++;
    String two = "" + c + current();
    switch (two) {
        case "!=": pos++; return new Token(TokenType.NEQ, "!=");
        case "<=": pos++; return new Token(TokenType.LTE, "<=");
        case ">=": pos++; return new Token(TokenType.GTE, ">=");
    }
    return switch (c) {
        case '=' -> new Token(TokenType.EQ,       "=");
        case '<' -> new Token(TokenType.LT,        "<");
        case '>' -> new Token(TokenType.GT,        ">");
        case ',' -> new Token(TokenType.COMMA,     ",");
        case ';' -> new Token(TokenType.SEMICOLON, ";");
        case '(' -> new Token(TokenType.LPAREN,    "(");
        case ')' -> new Token(TokenType.RPAREN,    ")");
        case '*' -> new Token(TokenType.ASTERISK,  "*");
        case '.' -> new Token(TokenType.DOT,       ".");
        default  -> throw new RuntimeException(
                        "Unexpected character: '" + c + "' at position " + (pos - 1));
    };
}
```

### Example Output

Running the lexer on the query:

```sql
SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;
```

Produces the following token stream:

```
Token(SELECT)
Token(IDENTIFIER, "name")
Token(COMMA, ",")
Token(IDENTIFIER, "age")
Token(FROM)
Token(IDENTIFIER, "users")
Token(WHERE)
Token(IDENTIFIER, "age")
Token(GTE, ">=")
Token(FLOAT, "18.5")
Token(AND)
Token(IDENTIFIER, "active")
Token(EQ, "=")
Token(BOOLEAN, "TRUE")
Token(SEMICOLON, ";")
Token(EOF)
```
 
---

## Conclusions

This laboratory work resulted in a fully functional lexer for a subset of SQL, covering the most common statement types: `SELECT`, `INSERT`, `UPDATE`, and `DELETE`. The implementation demonstrates the core concepts of lexical analysis in a clean and readable way.

The main design decisions made during the implementation were:

- Using a **sentinel character** `'\0'` appended to the input, eliminating bounds checks from every inner loop and keeping the helper methods free of defensive code. This is the same technique used by the Clang compiler in its production lexer.
- Centralizing the **single-character lookahead** dispatch in `tokenize()`, making the entry point easy to read and extend.
- Keeping the **keyword set as a static constant**, so the `O(1)` hash lookup happens once per word token rather than performing sequential comparisons against every keyword string.
- Treating **boolean literals separately** from structural keywords, since `TRUE` and `FALSE` carry a meaningful value that needs to be preserved in the token metadata.

Through this work, it became clear that a lexer is essentially a formalization of rules that are often applied intuitively when reading code. Every design decision — how to detect a float vs. an integer, how to handle the end of input, how to distinguish a keyword from an identifier — maps directly to a concrete rule in the language's definition.

## References

1. Lattner C., Tryzelaar E. "Kaleidoscope: Kaleidoscope Introduction and the Lexer". https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html

2. Wikipedia, "Lexical Analysis". https://en.wikipedia.org/wiki/Lexical_analysis