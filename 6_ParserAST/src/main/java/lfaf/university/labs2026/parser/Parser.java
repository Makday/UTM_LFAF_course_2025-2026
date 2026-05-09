package lfaf.university.labs2026.parser;

import java.util.ArrayList;
import java.util.List;

import lfaf.university.labs2026.Token;
import lfaf.university.labs2026.TokenType;
import lfaf.university.labs2026.ast.Ast;

public class Parser {
    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = List.copyOf(tokens);
        this.pos = 0;
    }

    public Ast.SqlProgram parseProgram() {
        List<Ast.Statement> statements = new ArrayList<>();

        if (check(TokenType.EOF)) {
            throw error("Input is empty");
        }

        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
            if (match(TokenType.SEMICOLON)) {
                continue;
            }
            if (!check(TokenType.EOF)) {
                throw error("Expected ';' or end of input after statement");
            }
        }

        return new Ast.SqlProgram(statements);
    }

    private Ast.Statement parseStatement() {
        if (match(TokenType.SELECT)) {
            return parseSelect();
        }
        if (match(TokenType.INSERT)) {
            return parseInsert();
        }
        if (match(TokenType.UPDATE)) {
            return parseUpdate();
        }
        if (match(TokenType.DELETE)) {
            return parseDelete();
        }
        throw error("Expected a SQL statement keyword");
    }

    private Ast.SelectStatement parseSelect() {
        boolean selectAll = false;
        List<String> columns = new ArrayList<>();

        if (match(TokenType.ASTERISK)) {
            selectAll = true;
        } else {
            columns.add(parseName());
            while (match(TokenType.COMMA)) {
                columns.add(parseName());
            }
        }

        expect(TokenType.FROM, "Expected FROM after SELECT list");
        String tableName = parseName();

        Ast.Expression whereClause = null;
        List<Ast.OrderByItem> orderBy = List.of();
        Integer limit = null;

        while (true) {
            if (match(TokenType.WHERE)) {
                whereClause = parseExpression();
                continue;
            }
            if (match(TokenType.ORDER)) {
                expect(TokenType.BY, "Expected BY after ORDER");
                orderBy = parseOrderByList();
                continue;
            }
            if (match(TokenType.LIMIT)) {
                Token number = expect(TokenType.INTEGER, "Expected integer after LIMIT");
                limit = Integer.valueOf(number.getMeta());
                continue;
            }
            break;
        }

        return new Ast.SelectStatement(selectAll, columns, tableName, whereClause, orderBy, limit);
    }

    private Ast.InsertStatement parseInsert() {
        expect(TokenType.INTO, "Expected INTO after INSERT");
        String tableName = parseName();

        List<String> columns = List.of();
        if (match(TokenType.LPAREN)) {
            columns = parseNameList(TokenType.RPAREN);
        }

        expect(TokenType.VALUES, "Expected VALUES after INSERT target");
        expect(TokenType.LPAREN, "Expected '(' before INSERT values");
        List<Ast.Expression> values = parseExpressionList(TokenType.RPAREN);

        return new Ast.InsertStatement(tableName, columns, values);
    }

    private Ast.UpdateStatement parseUpdate() {
        String tableName = parseName();
        expect(TokenType.SET, "Expected SET after UPDATE target");

        List<Ast.Assignment> assignments = new ArrayList<>();
        assignments.add(parseAssignment());
        while (match(TokenType.COMMA)) {
            assignments.add(parseAssignment());
        }

        Ast.Expression whereClause = null;
        if (match(TokenType.WHERE)) {
            whereClause = parseExpression();
        }

        return new Ast.UpdateStatement(tableName, assignments, whereClause);
    }

    private Ast.DeleteStatement parseDelete() {
        expect(TokenType.FROM, "Expected FROM after DELETE");
        String tableName = parseName();

        Ast.Expression whereClause = null;
        if (match(TokenType.WHERE)) {
            whereClause = parseExpression();
        }

        return new Ast.DeleteStatement(tableName, whereClause);
    }

    private Ast.Assignment parseAssignment() {
        String target = parseName();
        expect(TokenType.EQ, "Expected '=' in assignment");
        Ast.Expression value = parseExpression();
        return new Ast.Assignment(target, value);
    }

    private List<Ast.OrderByItem> parseOrderByList() {
        List<Ast.OrderByItem> items = new ArrayList<>();
        items.add(parseOrderByItem());
        while (match(TokenType.COMMA)) {
            items.add(parseOrderByItem());
        }
        return items;
    }

    private Ast.OrderByItem parseOrderByItem() {
        String expression = parseName();
        boolean ascending = true;
        if (match(TokenType.ASC)) {
            ascending = true;
        } else if (match(TokenType.DESC)) {
            ascending = false;
        }
        return new Ast.OrderByItem(expression, ascending);
    }

    private List<String> parseNameList(TokenType closingToken) {
        List<String> names = new ArrayList<>();
        if (check(closingToken)) {
            throw error("Expected at least one identifier inside parentheses");
        }
        names.add(parseName());
        while (match(TokenType.COMMA)) {
            names.add(parseName());
        }
        expect(closingToken, "Expected closing token after identifier list");
        return names;
    }

    private List<Ast.Expression> parseExpressionList(TokenType closingToken) {
        List<Ast.Expression> expressions = new ArrayList<>();
        if (check(closingToken)) {
            throw error("Expected at least one expression inside parentheses");
        }
        expressions.add(parseExpression());
        while (match(TokenType.COMMA)) {
            expressions.add(parseExpression());
        }
        expect(closingToken, "Expected closing token after expression list");
        return expressions;
    }

    private Ast.Expression parseExpression() {
        return parseOr();
    }

    private Ast.Expression parseOr() {
        Ast.Expression expression = parseAnd();
        while (match(TokenType.OR)) {
            expression = new Ast.BinaryExpression(expression, previous().getType(), parseAnd());
        }
        return expression;
    }

    private Ast.Expression parseAnd() {
        Ast.Expression expression = parseUnary();
        while (match(TokenType.AND)) {
            expression = new Ast.BinaryExpression(expression, previous().getType(), parseUnary());
        }
        return expression;
    }

    private Ast.Expression parseUnary() {
        if (match(TokenType.NOT)) {
            return new Ast.UnaryExpression(previous().getType(), parseUnary());
        }
        return parseComparison();
    }

    private Ast.Expression parseComparison() {
        Ast.Expression expression = parsePrimary();
        if (match(TokenType.EQ, TokenType.NEQ, TokenType.LT, TokenType.GT, TokenType.LTE, TokenType.GTE)) {
            TokenType operator = previous().getType();
            Ast.Expression right = parsePrimary();
            return new Ast.BinaryExpression(expression, operator, right);
        }
        return expression;
    }

    private Ast.Expression parsePrimary() {
        if (match(TokenType.LPAREN)) {
            Ast.Expression inner = parseExpression();
            expect(TokenType.RPAREN, "Expected ')' after expression");
            return inner;
        }

        if (match(TokenType.INTEGER)) {
            return new Ast.LiteralExpression(previous().getMeta(), Ast.LiteralType.INTEGER);
        }
        if (match(TokenType.FLOAT)) {
            return new Ast.LiteralExpression(previous().getMeta(), Ast.LiteralType.FLOAT);
        }
        if (match(TokenType.STRING)) {
            return new Ast.LiteralExpression(previous().getMeta(), Ast.LiteralType.STRING);
        }
        if (match(TokenType.BOOLEAN)) {
            return new Ast.LiteralExpression(previous().getMeta(), Ast.LiteralType.BOOLEAN);
        }
        if (check(TokenType.IDENTIFIER)) {
            return parseNameExpression();
        }

        throw error("Expected an expression");
    }

    private Ast.Expression parseNameExpression() {
        List<String> parts = parseNameParts();
        if (parts.size() == 1) {
            return new Ast.IdentifierExpression(parts.get(0));
        }
        return new Ast.QualifiedNameExpression(parts);
    }

    private String parseName() {
        return String.join(".", parseNameParts());
    }

    private List<String> parseNameParts() {
        List<String> parts = new ArrayList<>();
        parts.add(expect(TokenType.IDENTIFIER, "Expected identifier").getMeta());
        while (match(TokenType.DOT)) {
            parts.add(expect(TokenType.IDENTIFIER, "Expected identifier after '.'").getMeta());
        }
        return parts;
    }

    private Token expect(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(message + ", found " + describe(current()));
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return current().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            pos++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return current().getType() == TokenType.EOF;
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private ParseException error(String message) {
        return new ParseException(message + " at token " + pos + " near " + describe(current()));
    }

    private String describe(Token token) {
        return token.getMeta() == null ? token.getType().name() : token.getType() + "(\"" + token.getMeta() + "\")";
    }
}

