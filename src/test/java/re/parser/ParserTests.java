package re.parser;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import re.parser.token.*;
import re.parser.token.Token;

public class ParserTests {
    @Test
    public void insertConcatenationsTest() {
        // set up actual tokens
        List<Token> actual = new ArrayList<Token>();
        actual.add(new Literal("a"));
        actual.add(new Asterisk());
        actual.add(new Literal("b"));
        actual.add(new Literal("c"));
        actual.add(new Literal("d"));
        actual.add(new Asterisk());
        actual = Parser.insert(actual, new Concat());
        // set up expected tokens
        List<Token> expected = new ArrayList<Token>();
        expected.add(new Literal("a"));
        expected.add(new Asterisk());
        expected.add(new Concat());
        expected.add(new Literal("b"));
        expected.add(new Concat());
        expected.add(new Literal("c"));
        expected.add(new Concat());
        expected.add(new Literal("d"));
        expected.add(new Asterisk());

        assertEquals(expected, actual);
    }
}