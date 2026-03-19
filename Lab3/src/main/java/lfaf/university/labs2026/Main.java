package lfaf.university.labs2026;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String query = "SELECT name, age FROM users WHERE age >= 18.5 AND active = TRUE;";
        Lexer lexer = new Lexer(query);
        List<Token> tokens = lexer.tokenize();
        tokens.forEach(System.out::println);
    }
}
