package parser.token;

public class Concat implements Token {
    static final int priority = 2;
    private Token first;
    private Token second;

    public Concat(Token f, Token s) {
        first = f;
        second = s;
    }

    public void apply() {

    }
}