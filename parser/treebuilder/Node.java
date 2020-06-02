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

    void setParent(Node<T> p) {
        parent = p;
    }

    void addChild(T c) {
        Node<T> childNode = new Node<T>(c);
        children.add(childNode);
        childNode.setParent(this);
    }

    void addChild(Node<T> c) {
        if (c != null) {
            children.add(c);
            c.setParent(this);
        }
    }
}