package parser.token;

public interface Token {
    int priority = 0;

    public void apply();
}