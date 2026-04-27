package lfaf.university.labs2026;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public Lexer() {
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();

        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);

            switch (c) {
                case '(' -> {
                    tokens.add(new Token(TokenType.LPAREN));
                    i++;
                }
                case ')' -> {
                    tokens.add(new Token(TokenType.RPAREN));
                    i++;
                }
                case '|' -> {
                    tokens.add(new Token(TokenType.PIPE));
                    i++;
                }
                case '+' -> {
                    tokens.add(new Token(TokenType.PLUS));
                    i++;
                }
                case '*' -> {
                    tokens.add(new Token(TokenType.STAR));
                    i++;
                }
                case '?' -> {
                    tokens.add(new Token(TokenType.QUESTION));
                    i++;
                }

                case '{' -> {
                    i++; // skip '{'
                    StringBuilder number = new StringBuilder();

                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        number.append(input.charAt(i));
                        i++;
                    }

                    if (i >= input.length() || input.charAt(i) != '}') {
                        throw new IllegalArgumentException("Unclosed quantifier: missing '}'");
                    }

                    i++; // skip '}'

                    if (number.isEmpty()) {
                        throw new IllegalArgumentException("Empty quantifier {} is not allowed");
                    }

                    tokens.add(new Token(TokenType.QUANTITY, number.toString()));
                }

                default -> {
                    tokens.add(new Token(TokenType.CHAR, String.valueOf(c)));
                    i++;
                }
            }
        }

        return tokens;
    }
}