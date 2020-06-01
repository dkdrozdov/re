package parser.token;

public class Concat implements Token {
    private Token first;
    private Token second;

    @Override
    public int getPriority() {

        return 2;
    }

    public Concat(Token f, Token s) {
        first = f;
        second = s;
    }

    public void apply() {

    }
}