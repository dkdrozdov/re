package re.fa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StateTableTests {
    @Test
    public void concatenateStateTableTest() {
        // set up table1
        StateTable table1 = new StateTable();
        table1.finalState = table1.addState();
        table1.addTransitionLiteral("a");
        table1.addTransitionLiteral("b");
        table1.addTransitionLiteral("c");
        table1.addTransition("a", 0, 1);
        table1.addTransition("b", 0, 2);
        table1.addTransition("c", 0, 1);
        table1.addTransition("a", 1, 2);
        table1.addTransition("b", 1, 1);
        table1.addTransition("c", 1, 2);
        // set up table2
        StateTable table2 = new StateTable();
        table2.finalState = table2.addState();
        table2.addTransitionLiteral("d");
        table2.addTransitionLiteral("a");
        table2.addTransition("d", 0, 1);
        table2.addTransition("a", 1, 2);
        // set up expected table
        StateTable expectedTable = new StateTable();
        expectedTable.addState();
        expectedTable.addState();
        expectedTable.finalState = expectedTable.addState();
        expectedTable.addTransitionLiteral("a");
        expectedTable.addTransitionLiteral("b");
        expectedTable.addTransitionLiteral("c");
        expectedTable.addTransitionLiteral("d");
        expectedTable.addTransition("a", 0, 1);
        expectedTable.addTransition("a", 1, 2);
        expectedTable.addTransition("a", 3, 4);
        expectedTable.addTransition("b", 0, 2);
        expectedTable.addTransition("b", 1, 1);
        expectedTable.addTransition("c", 0, 1);
        expectedTable.addTransition("c", 1, 2);
        expectedTable.addTransition("d", 2, 3);
        // set up actual table
        StateTable actualTable = new StateTable(table1);
        actualTable.concatenateStateTable(table2);

        assertEquals(expectedTable, actualTable);
    }
}
