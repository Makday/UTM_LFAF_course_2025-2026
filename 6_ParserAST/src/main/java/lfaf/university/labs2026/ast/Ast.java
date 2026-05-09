package lfaf.university.labs2026.ast;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import lfaf.university.labs2026.TokenType;

public final class Ast {
    private Ast() {
    }

    public interface AstNode {
    }

    public interface Statement extends AstNode {
    }

    public interface Expression extends AstNode {
    }

    public enum LiteralType {
        INTEGER,
        FLOAT,
        STRING,
        BOOLEAN
    }

    public record SqlProgram(List<Statement> statements) implements AstNode {
        public SqlProgram {
            statements = List.copyOf(Objects.requireNonNull(statements, "statements"));
        }

        @Override
        public String toString() {
            if (statements.isEmpty()) {
                return "<empty program>";
            }
            if (statements.size() == 1) {
                return statements.get(0).toString();
            }
            return statements.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(";\n", "", ";"));
        }
    }

    public record SelectStatement(
            boolean selectAll,
            List<String> columns,
            String tableName,
            Expression whereClause,
            List<OrderByItem> orderBy,
            Integer limit
    ) implements Statement {
        public SelectStatement {
            columns = List.copyOf(Objects.requireNonNull(columns, "columns"));
            orderBy = List.copyOf(Objects.requireNonNull(orderBy, "orderBy"));
            tableName = Objects.requireNonNull(tableName, "tableName");
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("SELECT ");
            if (selectAll) {
                builder.append("*");
            } else {
                builder.append(String.join(", ", columns));
            }
            builder.append(" FROM ").append(tableName);
            if (whereClause != null) {
                builder.append(" WHERE ").append(whereClause);
            }
            if (!orderBy.isEmpty()) {
                builder.append(" ORDER BY ");
                builder.append(orderBy.stream().map(Object::toString).collect(Collectors.joining(", ")));
            }
            if (limit != null) {
                builder.append(" LIMIT ").append(limit);
            }
            return builder.toString();
        }
    }

    public record InsertStatement(
            String tableName,
            List<String> columns,
            List<Expression> values
    ) implements Statement {
        public InsertStatement {
            tableName = Objects.requireNonNull(tableName, "tableName");
            columns = List.copyOf(Objects.requireNonNull(columns, "columns"));
            values = List.copyOf(Objects.requireNonNull(values, "values"));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("INSERT INTO ").append(tableName);
            if (!columns.isEmpty()) {
                builder.append(" (").append(String.join(", ", columns)).append(")");
            }
            builder.append(" VALUES (");
            builder.append(values.stream().map(Object::toString).collect(Collectors.joining(", ")));
            builder.append(")");
            return builder.toString();
        }
    }

    public record UpdateStatement(
            String tableName,
            List<Assignment> assignments,
            Expression whereClause
    ) implements Statement {
        public UpdateStatement {
            tableName = Objects.requireNonNull(tableName, "tableName");
            assignments = List.copyOf(Objects.requireNonNull(assignments, "assignments"));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            builder.append(assignments.stream().map(Object::toString).collect(Collectors.joining(", ")));
            if (whereClause != null) {
                builder.append(" WHERE ").append(whereClause);
            }
            return builder.toString();
        }
    }

    public record DeleteStatement(
            String tableName,
            Expression whereClause
    ) implements Statement {
        public DeleteStatement {
            tableName = Objects.requireNonNull(tableName, "tableName");
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("DELETE FROM ").append(tableName);
            if (whereClause != null) {
                builder.append(" WHERE ").append(whereClause);
            }
            return builder.toString();
        }
    }

    public record Assignment(String target, Expression value) {
        public Assignment {
            target = Objects.requireNonNull(target, "target");
            value = Objects.requireNonNull(value, "value");
        }

        @Override
        public String toString() {
            return target + " = " + value;
        }
    }

    public record OrderByItem(String expression, boolean ascending) {
        public OrderByItem {
            expression = Objects.requireNonNull(expression, "expression");
        }

        @Override
        public String toString() {
            return expression + (ascending ? " ASC" : " DESC");
        }
    }

    public record IdentifierExpression(String name) implements Expression {
        public IdentifierExpression {
            name = Objects.requireNonNull(name, "name");
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public record QualifiedNameExpression(List<String> parts) implements Expression {
        public QualifiedNameExpression {
            parts = List.copyOf(Objects.requireNonNull(parts, "parts"));
            if (parts.isEmpty()) {
                throw new IllegalArgumentException("Qualified name must not be empty");
            }
        }

        @Override
        public String toString() {
            return String.join(".", parts);
        }
    }

    public record LiteralExpression(String value, LiteralType type) implements Expression {
        public LiteralExpression {
            value = Objects.requireNonNull(value, "value");
            type = Objects.requireNonNull(type, "type");
        }

        @Override
        public String toString() {
            return switch (type) {
                case STRING -> "'" + value.replace("'", "''") + "'";
                case BOOLEAN -> value.toUpperCase();
                default -> value;
            };
        }
    }

    public record UnaryExpression(TokenType operator, Expression operand) implements Expression {
        public UnaryExpression {
            operator = Objects.requireNonNull(operator, "operator");
            operand = Objects.requireNonNull(operand, "operand");
        }

        @Override
        public String toString() {
            return operator + " " + operand;
        }
    }

    public record BinaryExpression(Expression left, TokenType operator, Expression right) implements Expression {
        public BinaryExpression {
            left = Objects.requireNonNull(left, "left");
            operator = Objects.requireNonNull(operator, "operator");
            right = Objects.requireNonNull(right, "right");
        }

        @Override
        public String toString() {
            return '(' + left.toString() + ' ' + operator + ' ' + right + ')';
        }
    }
}

