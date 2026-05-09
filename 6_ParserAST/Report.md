# 6_ParserAST

### Course: Formal Languages & Finite Automata
### Author: Bruma Cristian

----

## Objectives

1. Get familiar with parsing and how it can be implemented in code.
2. Get familiar with the concept of an Abstract Syntax Tree (AST).
3. Extend the previous lexical analysis work with:
   1. A `TokenType` enum for token categorization.
   2. Regular-expression based token recognition.
   3. AST data structures for the processed language.
   4. A simple parser that extracts syntactic information from the input text.

## Implementation Description

This project implements a small SQL-like front end in Java. It consists of three main stages:

1. `Lexer` — converts raw text into a list of tokens.
2. `Parser` — consumes the token stream and builds an AST.
3. `Ast` — stores the parsed structure in a hierarchical form.

The supported language subset includes:

- `SELECT ... FROM ... [WHERE ...] [ORDER BY ...] [LIMIT ...]`
- `INSERT INTO ... VALUES ...`
- `UPDATE ... SET ... [WHERE ...]`
- `DELETE FROM ... [WHERE ...]`

### TokenType and Token

`TokenType` is an enumeration that groups all token categories used by the lexer:

- SQL keywords such as `SELECT`, `FROM`, `WHERE`, `AND`, `OR`, `NOT`
- data literals: `INTEGER`, `FLOAT`, `STRING`, `BOOLEAN`
- identifiers and punctuation/operators
- the terminal `EOF` token

`Token` is a lightweight immutable object containing the token type and optional metadata. Structural tokens do not need metadata, while identifiers and literals preserve their raw value.

```java
public enum TokenType {
    SELECT, FROM, WHERE, AND, OR, NOT,
    INSERT, INTO, VALUES,
    UPDATE, SET,
    DELETE,
    ORDER, BY, ASC, DESC,
    LIMIT,

    INTEGER, FLOAT, STRING, BOOLEAN,
    IDENTIFIER,

    EQ, NEQ, LT, GT, LTE, GTE,
    COMMA, SEMICOLON, LPAREN, RPAREN, ASTERISK, DOT,

    EOF
}
```

### Lexer

The lexer recognizes tokens using regular expressions. The main patterns are:

- whitespace
- string literals
- floating-point numbers
- integers
- identifiers
- symbols/operators

Keyword recognition is done after an identifier match by converting the lexeme to uppercase and looking it up in a keyword map. This keeps the lexer case-insensitive for SQL keywords while preserving the original spelling of identifiers.

```java
private Token readToken() {
    String lexeme;

    if ((lexeme = match(STRING)) != null) {
        return new Token(TokenType.STRING, unquote(lexeme));
    }
    if ((lexeme = match(FLOAT)) != null) {
        return new Token(TokenType.FLOAT, lexeme);
    }
    if ((lexeme = match(IDENTIFIER)) != null) {
        TokenType keyword = KEYWORDS.get(lexeme.toUpperCase());
        if (keyword != null) {
            return keyword == TokenType.BOOLEAN
                    ? new Token(TokenType.BOOLEAN, lexeme.toUpperCase())
                    : new Token(keyword);
        }
        return new Token(TokenType.IDENTIFIER, lexeme);
    }

    if ((lexeme = match(SYMBOL)) != null) {
        return switch (lexeme) {
            case "=" -> new Token(TokenType.EQ, lexeme);
            case "!=" -> new Token(TokenType.NEQ, lexeme);
            case "<" -> new Token(TokenType.LT, lexeme);
            case ">" -> new Token(TokenType.GT, lexeme);
            case "<=" -> new Token(TokenType.LTE, lexeme);
            case ">=" -> new Token(TokenType.GTE, lexeme);
            case "," -> new Token(TokenType.COMMA, lexeme);
            case ";" -> new Token(TokenType.SEMICOLON, lexeme);
            case "(" -> new Token(TokenType.LPAREN, lexeme);
            case ")" -> new Token(TokenType.RPAREN, lexeme);
            case "*" -> new Token(TokenType.ASTERISK, lexeme);
            case "." -> new Token(TokenType.DOT, lexeme);
            default -> throw new IllegalStateException("Unexpected symbol lexeme: " + lexeme);
        };
    }

    if (input.charAt(pos) == '\'') {
        throw new RuntimeException("Unterminated string at position " + pos);
    }

    throw new RuntimeException("Unexpected character: '" + input.charAt(pos) + "' at position " + pos);
}
```

Example query:

```sql
SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;
```

The lexer produces tokens like:

```text
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

### AST

The AST is implemented in `lfaf.university.labs2026.ast.Ast` and contains nested node types for:

- program root: `SqlProgram`
- statements: `SelectStatement`, `InsertStatement`, `UpdateStatement`, `DeleteStatement`
- expressions: `IdentifierExpression`, `QualifiedNameExpression`, `LiteralExpression`, `UnaryExpression`, `BinaryExpression`
- helper nodes: `Assignment`, `OrderByItem`

The AST is intentionally more abstract than the token stream. For example, the parser does not keep punctuation tokens in the final tree; instead, it stores meaningful constructs like assignments, comparison expressions, and ordering items.

```java
public record SqlProgram(List<Statement> statements) implements AstNode { }

public record SelectStatement(
        boolean selectAll,
        List<String> columns,
        String tableName,
        Expression whereClause,
        List<OrderByItem> orderBy,
        Integer limit
) implements Statement { }

public record BinaryExpression(Expression left, TokenType operator, Expression right)
        implements Expression { }
```

### Parser

The parser uses a recursive-descent approach. It reads the token list from left to right and applies grammar rules with precedence handling.

Expression precedence is implemented as follows:

1. `NOT`
2. comparison operators (`=`, `!=`, `<`, `<=`, `>`, `>=`)
3. `AND`
4. `OR`

This ensures that expressions such as:

```java
private Ast.Expression parseOr() {
    Ast.Expression expression = parseAnd();
    while (match(TokenType.OR)) {
        expression = new Ast.BinaryExpression(expression, previous().getType(), parseAnd());
    }
    return expression;
}
```

```sql
NOT active OR age >= 18 AND verified = TRUE
```

are grouped correctly in the AST.

### Example AST Output

For the sample input, the parser builds an AST equivalent to:

```text
SELECT name, age FROM users WHERE ((age GTE 18.5) AND (active EQ TRUE))
```

The parser also supports the other statement types used in the project:

```java
Parser parser = new Parser(tokens);
Ast.SqlProgram program = parser.parseProgram();

System.out.println("AST tree:");
printProgram(program, "");
```

```sql
INSERT INTO users (name, age) VALUES ('Alice', 20);
UPDATE users SET active = FALSE, age = 21 WHERE name = 'Alice';
DELETE FROM users WHERE active = FALSE;
```

## Testing

The project includes JUnit 5 tests that verify:

- tokenization of a representative `SELECT` query
- AST construction for `SELECT`, `INSERT`, `UPDATE`, and `DELETE`
- operator precedence for boolean expressions
- failure on empty input

```java
@Test
void parsesSelectIntoAst() {
    Parser parser = new Parser(new Lexer("SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;").tokenize());
    Ast.SqlProgram program = parser.parseProgram();
    Ast.SelectStatement statement = assertInstanceOf(Ast.SelectStatement.class, program.statements().getFirst());
    assertEquals("users", statement.tableName());
}
```

The tests are executed with:

```bash
mvn test
```

## Conclusions

This laboratory work extends the previous lexer with a functional parser and AST representation for a SQL-like language. The main result is a clean pipeline from raw text to syntax tree, which is the foundation for later compiler or interpreter stages.

The most important implementation decisions were:

- using regular expressions for token recognition;
- separating lexical analysis from syntactic analysis;
- representing syntax with a structured AST instead of raw token lists;
- implementing recursive-descent parsing with operator precedence.

This work demonstrates how parsing transforms a flat token stream into a meaningful hierarchical structure.

## References

1. Wikipedia, "Parsing". https://en.wikipedia.org/wiki/Parsing
2. Wikipedia, "Abstract syntax tree". https://en.wikipedia.org/wiki/Abstract_syntax_tree
3. Oracle, "Regular Expressions in Java". https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
