import nfa.*;

public class test {
    public static void main(String[] args) {
        StateTable testTable = new StateTable();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addInputLit('a');
        testTable.addInputLit('b');
        testTable.addInputLit('c');
        testTable.addInputLit('d');
        // 0
        testTable.addTransition('a', 0, 1);
        testTable.addTransition('c', 0, 2);
        // 1
        testTable.addTransition('b', 1, 3);
        testTable.addTransition('d', 1, 2);
        // 2
        testTable.addTransition('c', 2, 1);
        testTable.addTransition('d', 2, 0);
        // 3
        testTable.addTransition('a', 3, 1);
        testTable.addTransition('d', 3, 3);

        StateTable anotherTable = new StateTable();
        anotherTable.addState();
        anotherTable.addState();
        anotherTable.addInputLit('y');
        anotherTable.addInputLit('e');
        anotherTable.addInputLit('a');
        anotherTable.addInputLit('g');
        // 0
        anotherTable.addTransition('y', 0, 1);
        anotherTable.addTransition('a', 0, 1);
        anotherTable.addTransition('g', 0, 2);
        // 1
        anotherTable.addTransition('y', 1, 2);
        anotherTable.addTransition('e', 1, 0);
        anotherTable.addTransition('a', 1, 1);
        anotherTable.addTransition('g', 1, 2);
        // 2
        anotherTable.addTransition('e', 2, 0);
        anotherTable.addTransition('g', 2, 1);

        testTable.concatenateStateTable(anotherTable);
    }
}