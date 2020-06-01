package parser.token;

public interface Token {
    static final int priority = 0;

    public void apply();
}