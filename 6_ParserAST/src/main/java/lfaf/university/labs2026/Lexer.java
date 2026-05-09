package lfaf.university.labs2026;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String input;
    private int pos;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern STRING = Pattern.compile("'(?:''|[^'])*'");
    private static final Pattern FLOAT = Pattern.compile("\\d+\\.\\d+");
    private static final Pattern INTEGER = Pattern.compile("\\d+");
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final Pattern SYMBOL = Pattern.compile("!=|<=|>=|=|<|>|,|;|\\(|\\)|\\*|\\.");

    private static final Map<String, TokenType> KEYWORDS = createKeywords();

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            skipWhitespace();
            if (pos >= input.length()) {
                break;
            }

            tokens.add(readToken());
        }

        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }

    private Token readToken() {
        String lexeme;

        if ((lexeme = match(STRING)) != null) {
            return new Token(TokenType.STRING, unquote(lexeme));
        }

        if ((lexeme = match(FLOAT)) != null) {
            return new Token(TokenType.FLOAT, lexeme);
        }

        if ((lexeme = match(INTEGER)) != null) {
            return new Token(TokenType.INTEGER, lexeme);
        }

        if ((lexeme = match(IDENTIFIER)) != null) {
            TokenType keyword = KEYWORDS.get(lexeme.toUpperCase());
            if (keyword != null) {
                if (keyword == TokenType.BOOLEAN) {
                    return new Token(TokenType.BOOLEAN, lexeme.toUpperCase());
                }
                return new Token(keyword);
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

    private void skipWhitespace() {
        match(WHITESPACE);
    }

    private String match(Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        matcher.region(pos, input.length());
        if (!matcher.lookingAt()) {
            return null;
        }
        String lexeme = matcher.group();
        pos += lexeme.length();
        return lexeme;
    }

    private String unquote(String lexeme) {
        return lexeme.substring(1, lexeme.length() - 1).replace("''", "'");
    }

    private static Map<String, TokenType> createKeywords() {
        Map<String, TokenType> keywords = new HashMap<>();
        keywords.put("SELECT", TokenType.SELECT);
        keywords.put("FROM", TokenType.FROM);
        keywords.put("WHERE", TokenType.WHERE);
        keywords.put("AND", TokenType.AND);
        keywords.put("OR", TokenType.OR);
        keywords.put("NOT", TokenType.NOT);
        keywords.put("INSERT", TokenType.INSERT);
        keywords.put("INTO", TokenType.INTO);
        keywords.put("VALUES", TokenType.VALUES);
        keywords.put("UPDATE", TokenType.UPDATE);
        keywords.put("SET", TokenType.SET);
        keywords.put("DELETE", TokenType.DELETE);
        keywords.put("ORDER", TokenType.ORDER);
        keywords.put("BY", TokenType.BY);
        keywords.put("ASC", TokenType.ASC);
        keywords.put("DESC", TokenType.DESC);
        keywords.put("LIMIT", TokenType.LIMIT);
        keywords.put("TRUE", TokenType.BOOLEAN);
        keywords.put("FALSE", TokenType.BOOLEAN);
        return keywords;
    }
}
