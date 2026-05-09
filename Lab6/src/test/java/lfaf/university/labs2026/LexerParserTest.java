package lfaf.university.labs2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import lfaf.university.labs2026.ast.Ast;
import lfaf.university.labs2026.parser.ParseException;
import lfaf.university.labs2026.parser.Parser;

class LexerParserTest {
    @Test
    void tokenizesSelectQuery() {
        Lexer lexer = new Lexer("SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;");
        List<TokenType> types = lexer.tokenize().stream().map(Token::getType).collect(Collectors.toList());

        assertEquals(List.of(
                TokenType.SELECT,
                TokenType.IDENTIFIER,
                TokenType.COMMA,
                TokenType.IDENTIFIER,
                TokenType.FROM,
                TokenType.IDENTIFIER,
                TokenType.WHERE,
                TokenType.IDENTIFIER,
                TokenType.GTE,
                TokenType.FLOAT,
                TokenType.AND,
                TokenType.IDENTIFIER,
                TokenType.EQ,
                TokenType.BOOLEAN,
                TokenType.SEMICOLON,
                TokenType.EOF
        ), types);
    }

    @Test
    void parsesSelectIntoAst() {
        Parser parser = new Parser(new Lexer("SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;").tokenize());
        Ast.SqlProgram program = parser.parseProgram();

        assertEquals(1, program.statements().size());
        Ast.SelectStatement statement = assertInstanceOf(Ast.SelectStatement.class, program.statements().get(0));
        assertEquals(List.of("name", "age"), statement.columns());
        assertEquals("users", statement.tableName());
        assertEquals("SELECT name, age FROM users WHERE ((age GTE 18.5) AND (active EQ TRUE))", statement.toString());
    }

    @Test
    void parsesAllStatementTypes() {
        Ast.SqlProgram insertProgram = new Parser(new Lexer("INSERT INTO users (name, age) VALUES ('Alice', 20);").tokenize()).parseProgram();
        Ast.InsertStatement insert = assertInstanceOf(Ast.InsertStatement.class, insertProgram.statements().get(0));
        assertEquals(List.of("name", "age"), insert.columns());
        assertEquals(2, insert.values().size());

        Ast.SqlProgram updateProgram = new Parser(new Lexer("UPDATE users SET active = FALSE, age = 21 WHERE name = 'Alice';").tokenize()).parseProgram();
        Ast.UpdateStatement update = assertInstanceOf(Ast.UpdateStatement.class, updateProgram.statements().get(0));
        assertEquals(2, update.assignments().size());
        assertEquals("UPDATE users SET active = FALSE, age = 21 WHERE (name EQ 'Alice')", update.toString());

        Ast.SqlProgram deleteProgram = new Parser(new Lexer("DELETE FROM users WHERE active = FALSE;").tokenize()).parseProgram();
        Ast.DeleteStatement delete = assertInstanceOf(Ast.DeleteStatement.class, deleteProgram.statements().get(0));
        assertEquals("users", delete.tableName());
        assertEquals("DELETE FROM users WHERE (active EQ FALSE)", delete.toString());
    }

    @Test
    void respectsBooleanPrecedence() {
        Ast.SqlProgram program = new Parser(new Lexer("SELECT * FROM users WHERE NOT active OR age >= 18 AND verified = TRUE;").tokenize()).parseProgram();
        Ast.SelectStatement select = assertInstanceOf(Ast.SelectStatement.class, program.statements().get(0));
        Ast.BinaryExpression orExpression = assertInstanceOf(Ast.BinaryExpression.class, select.whereClause());
        assertEquals(TokenType.OR, orExpression.operator());
        assertInstanceOf(Ast.UnaryExpression.class, orExpression.left());
        Ast.BinaryExpression andExpression = assertInstanceOf(Ast.BinaryExpression.class, orExpression.right());
        assertEquals(TokenType.AND, andExpression.operator());
    }

    @Test
    void rejectsEmptyInput() {
        ParseException exception = assertThrows(ParseException.class, () -> new Parser(new Lexer("").tokenize()).parseProgram());
        assertEquals("Input is empty at token 0 near EOF", exception.getMessage());
    }
}

