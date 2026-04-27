package lfaf.university.labs2026;

public enum TokenType {
    CHAR,           // Regular character (A, B, 0, 1, etc.)
    LPAREN,         // (
    RPAREN,         // )
    PIPE,           // |
    QUESTION,       // ? (optional)
    STAR,           // * (zero or more)
    PLUS,           // + (one or more)
    QUANTITY,       // N TIMES
}
