package lfaf.university.labs2026;

import java.util.List;

import lfaf.university.labs2026.ast.Ast;
import lfaf.university.labs2026.parser.Parser;

public class Main {
    public static void main(String[] args) {
        String query = "SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;";
        Lexer lexer = new Lexer(query);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);

        Ast.SqlProgram program = parser.parseProgram();

        System.out.println("Tokens:");
        tokens.forEach(System.out::println);

        System.out.println();
        System.out.println("AST proof:");
        System.out.println("Root node: " + program.getClass().getSimpleName());
        System.out.println("Statements: " + program.statements().size());
        if (!program.statements().isEmpty()) {
            System.out.println("First statement type: " + program.statements().getFirst().getClass().getSimpleName());
        }
        System.out.println("AST tree:");
        printProgram(program, "");
    }

    private static void printProgram(Ast.SqlProgram program, String indent) {
        System.out.println(indent + "SqlProgram");
        for (Ast.Statement statement : program.statements()) {
            printStatement(statement, indent + "  ");
        }
    }

    private static void printStatement(Ast.Statement statement, String indent) {
        if (statement instanceof Ast.SelectStatement(
                boolean selectAll, List<String> columns, String tableName, Ast.Expression whereClause,
                List<Ast.OrderByItem> orderBy, Integer limit
        )) {
            System.out.println(indent + "SelectStatement");
            System.out.println(indent + "  columns:");
            if (selectAll) {
                System.out.println(indent + "    *");
            } else {
                for (String column : columns) {
                    System.out.println(indent + "    " + column);
                }
            }
            System.out.println(indent + "  table: " + tableName);
            if (whereClause != null) {
                System.out.println(indent + "  where:");
                printExpression(whereClause, indent + "    ");
            }
            if (!orderBy.isEmpty()) {
                System.out.println(indent + "  orderBy:");
                for (Ast.OrderByItem item : orderBy) {
                    System.out.println(indent + "    OrderByItem");
                    System.out.println(indent + "      expression: " + item.expression());
                    System.out.println(indent + "      direction: " + (item.ascending() ? "ASC" : "DESC"));
                }
            }
            if (limit != null) {
                System.out.println(indent + "  limit: " + limit);
            }
            return;
        }

        if (statement instanceof Ast.InsertStatement(
                String tableName, List<String> columns, List<Ast.Expression> values
        )) {
            System.out.println(indent + "InsertStatement");
            System.out.println(indent + "  table: " + tableName);
            if (!columns.isEmpty()) {
                System.out.println(indent + "  columns:");
                for (String column : columns) {
                    System.out.println(indent + "    " + column);
                }
            }
            System.out.println(indent + "  values:");
            for (Ast.Expression value : values) {
                printExpression(value, indent + "    ");
            }
            return;
        }

        if (statement instanceof Ast.UpdateStatement(
                String tableName, List<Ast.Assignment> assignments, Ast.Expression whereClause
        )) {
            System.out.println(indent + "UpdateStatement");
            System.out.println(indent + "  table: " + tableName);
            System.out.println(indent + "  assignments:");
            for (Ast.Assignment assignment : assignments) {
                System.out.println(indent + "    Assignment");
                System.out.println(indent + "      target: " + assignment.target());
                System.out.println(indent + "      value:");
                printExpression(assignment.value(), indent + "        ");
            }
            if (whereClause != null) {
                System.out.println(indent + "  where:");
                printExpression(whereClause, indent + "    ");
            }
            return;
        }

        if (statement instanceof Ast.DeleteStatement(String tableName, Ast.Expression whereClause)) {
            System.out.println(indent + "DeleteStatement");
            System.out.println(indent + "  table: " + tableName);
            if (whereClause != null) {
                System.out.println(indent + "  where:");
                printExpression(whereClause, indent + "    ");
            }
            return;
        }

        System.out.println(indent + statement.getClass().getSimpleName());
    }

    private static void printExpression(Ast.Expression expression, String indent) {
        if (expression instanceof Ast.IdentifierExpression(String name)) {
            System.out.println(indent + "IdentifierExpression: " + name);
            return;
        }

        if (expression instanceof Ast.QualifiedNameExpression(List<String> parts)) {
            System.out.println(indent + "QualifiedNameExpression: " + String.join(".", parts));
            return;
        }

        if (expression instanceof Ast.LiteralExpression(String value, Ast.LiteralType type)) {
            System.out.println(indent + "LiteralExpression: " + type + " = " + value);
            return;
        }

        if (expression instanceof Ast.UnaryExpression(TokenType operator, Ast.Expression operand)) {
            System.out.println(indent + "UnaryExpression: " + operator);
            printExpression(operand, indent + "  ");
            return;
        }

        if (expression instanceof Ast.BinaryExpression(Ast.Expression left, TokenType operator, Ast.Expression right)) {
            System.out.println(indent + "BinaryExpression: " + operator);
            System.out.println(indent + "  left:");
            printExpression(left, indent + "    ");
            System.out.println(indent + "  right:");
            printExpression(right, indent + "    ");
            return;
        }

        System.out.println(indent + expression.getClass().getSimpleName() + ": " + expression);
    }
}
