package lfaf.university.labs2026;

import lfaf.university.labs2026.nodes.RegexNode;

import java.util.List;
import java.util.Set;

public class RegexGenerator {
    public RegexNode parse(String input) {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    public Set<String> generate(String input) {
        return parse(input).generate();
    }
}


