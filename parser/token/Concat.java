package parser.token;

public class Concat implements Token {
    /*
     * private Token first; private Token second; public Concat(Token f, Token s) {
     * first = f; second = s; }
     */

    @Override
    public int getPriority() {

        return TokenPrioritiy.CONCAT.toInt();
    }

    public void apply() {

    }
}