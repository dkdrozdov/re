package parser.treebuilder;

import java.util.*;

public class Node<T> {
    List<Node<T>> children = new ArrayList<Node<T>>();
    Node<T> parent = null;
    T data = null;

    Node(T t) {
        data = t;
    }

    Node(T t, Node<T> p) {
        data = t;
        parent = p;
    }

    public void setParent(Node<T> p) {
        parent = p;
    }

    public void addChild(T c) {
        Node<T> childNode = new Node<T>(c);
        children.add(childNode);
        childNode.setParent(this);
    }

    public void addChild(Node<T> c) {
        if (c != null) {
            children.add(c);
            c.setParent(this);
        }
    }

    public boolean ifLeaf() {
        return (children.size() == 0);
    }

    public Node<T> getParent() {
        return this.parent;
    }

    public List<Node<T>> getChildren() {
        return this.children;
    }
}