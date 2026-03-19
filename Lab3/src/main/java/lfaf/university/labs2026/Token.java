package lfaf.university.labs2026;

public class Token {
    private final TokenType type;
    private final String meta;

    public Token(TokenType type, String meta) {
        this.type = type;
        this.meta = meta;
    }

    public Token(TokenType type) {
        this(type, null);
    }

    public TokenType getType() { return type; }
    public String getMeta() { return meta; }

    @Override
    public String toString() {
        return meta != null
                ? "Token(" + type + ", \"" + meta + "\")"
                : "Token(" + type + ")";
    }
}
