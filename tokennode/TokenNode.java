package tokennode;

public class TokenNode {
    List<TokenNode> children = new ArrayList<TokenNode>();
    TokenNode parent = null;
    Token data = null;

    TokenNode(Token t) {
        data = t;
    }

    TokenNode(Token t, TokenNode p) {
        data = t;
        parent = p;
    }

    void setParent(TokenNode p) {
        parent = p;
    }

    void addChildren(Token c) {
        TokenNode childrenNode = new TokenNode(c);
        children.add(childrenNode);
        childrenNode.setParent(this);
    }

}