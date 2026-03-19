package lfaf.university.labs2026;

public enum TokenType {
    SELECT, FROM, WHERE, AND, OR, NOT,
    INSERT, INTO, VALUES,
    UPDATE, SET,
    DELETE,
    ORDER, BY, ASC, DESC,
    LIMIT,

    INTEGER,
    FLOAT,
    STRING,
    BOOLEAN,

    IDENTIFIER,

    EQ,         // =
    NEQ,        // !=
    LT,         //
    GT,         // >
    LTE,        // <=
    GTE,        // >=

    COMMA,
    SEMICOLON,
    LPAREN,
    RPAREN,
    ASTERISK,
    DOT,

    EOF
}
