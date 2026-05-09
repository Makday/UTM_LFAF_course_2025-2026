package lfaf.university.labs2026;

import lfaf.university.labs2026.nodes.*;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public RegexNode parse() {
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Regex is empty");
        }

        RegexNode result = parseUnion();

        if (!isAtEnd()) {
            throw new IllegalArgumentException("Unexpected token: " + peek());
        }

        return result;
    }

    // union := concat ('|' concat)*
    private RegexNode parseUnion() {
        RegexNode left = parseConcat();

        while (match(TokenType.PIPE)) {
            RegexNode right = parseConcat();
            left = new UnionNode(left, right);
        }

        return left;
    }

    // concat := repeat+
    private RegexNode parseConcat() {
        RegexNode left = parseRepeat();

        while (startsAtom(peekType())) {
            RegexNode right = parseRepeat();
            left = new ConcatNode(left, right);
        }

        return left;
    }

    // repeat := atom ('*' | '+' | '?' | QUANTITY)?
    private RegexNode parseRepeat() {
        RegexNode node = parseAtom();

        if (match(TokenType.STAR)) {
            return new StarNode(node);
        }

        if (match(TokenType.PLUS)) {
            return new PlusNode(node);
        }

        if (match(TokenType.QUESTION)) {
            return new QuestionNode(node);
        }

        if (check(TokenType.QUANTITY)) {
            int count = parseQuantity(advance().getMeta());
            return new QuantityNode(node, count);
        }

        return node;
    }

    // atom := CHAR | '(' regex ')'
    private RegexNode parseAtom() {
        if (match(TokenType.CHAR)) {
            return new CharNode(previous().getMeta());
        }

        if (match(TokenType.LPAREN)) {
            if (check(TokenType.RPAREN)) {
                throw new IllegalArgumentException("Empty group '()' is not allowed");
            }

            RegexNode inner = parseUnion();
            consume(TokenType.RPAREN, "Missing ')'");
            return inner;
        }

        throw new IllegalArgumentException("Expected CHAR or '(' but found: " + describeCurrent());
    }

    private boolean startsAtom(TokenType type) {
        return type == TokenType.CHAR || type == TokenType.LPAREN;
    }

    private int parseQuantity(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid quantity: {" + raw + "}", ex);
        }
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private void consume(TokenType expected, String message) {
        if (!check(expected)) {
            throw new IllegalArgumentException(message + ", found: " + describeCurrent());
        }
        advance();
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            position++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return position >= tokens.size();
    }

    private Token peek() {
        return tokens.get(position);
    }

    private Token previous() {
        return tokens.get(position - 1);
    }

    private TokenType peekType() {
        return isAtEnd() ? null : peek().getType();
    }

    private String describeCurrent() {
        return isAtEnd() ? "end of input" : peek().toString();
    }
}
