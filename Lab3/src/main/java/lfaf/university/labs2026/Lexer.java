package lfaf.university.labs2026;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lexer {
    private final String input;
    private int pos;

    private static final Set<String> KEYWORDS = Set.of(
            "SELECT", "FROM", "WHERE", "AND", "OR", "NOT",
            "INSERT", "INTO", "VALUES",
            "UPDATE", "SET",
            "DELETE",
            "ORDER", "BY", "ASC", "DESC",
            "LIMIT", "TRUE", "FALSE"
    );

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            skipWhitespace();
            if (pos >= input.length()) break;

            char c = current();

            if (Character.isLetter(c) || c == '_') {
                tokens.add(readWord());
            } else if (Character.isDigit(c)) {
                tokens.add(readNumber());
            } else if (c == '\'') {
                tokens.add(readString());
            } else {
                tokens.add(readSymbol());
            }
        }

        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }

    private Token readWord() {
        int start = pos;
        while (pos < input.length() && (Character.isLetterOrDigit(current()) || current() == '_')) {
            pos++;
        }
        String word = input.substring(start, pos);
        String upper = word.toUpperCase();

        if (KEYWORDS.contains(upper)) {
            if (upper.equals("TRUE") || upper.equals("FALSE")) {
                return new Token(TokenType.BOOLEAN, upper);
            }
            return new Token(TokenType.valueOf(upper));
        }
        return new Token(TokenType.IDENTIFIER, word);
    }

    private Token readNumber() {
        int start = pos;
        boolean isFloat = false;

        while (pos < input.length() && Character.isDigit(current())) pos++;

        if (pos < input.length() && current() == '.' && pos + 1 < input.length() && Character.isDigit(input.charAt(pos + 1))) {
            isFloat = true;
            pos++;
            while (pos < input.length() && Character.isDigit(current())) pos++;
        }

        String value = input.substring(start, pos);
        return new Token(isFloat ? TokenType.FLOAT : TokenType.INTEGER, value);
    }

    private Token readString() {
        pos++;
        int start = pos;
        while (pos < input.length() && current() != '\'') {
            pos++;
        }
        String value = input.substring(start, pos);
        if (pos < input.length()) pos++; // skip closing '
        return new Token(TokenType.STRING, value);
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(current())) {
            pos++;
        }
    }

    private char current() {
        return input.charAt(pos);
    }

    private char peek() {
        if(pos + 1 < input.length()) return input.charAt(pos+1);
        else return current();
    }
}
