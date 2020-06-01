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

    void addChildren(T c) {
        Node<T> childrenNode = new Node<T>(c);
        children.add(childrenNode);
        childrenNode.setParent(this);
    }
}