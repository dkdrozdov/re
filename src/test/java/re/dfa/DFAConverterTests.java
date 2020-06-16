package re.dfa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import re.fa.SpecialTransitions;
import re.fa.StateTable;

public class DFAConverterTests {

    @Test
    public void convertToDFATest() {
        // set up test table
        StateTable table = new StateTable();
        table.addTransitionLiteral("a");
        table.addTransitionLiteral("b");
        table.addTransitionLiteral("c");
        table.addTransitionLiteral("0");
        table.addTransitionLiteral("1");
        table.addTransitionLiteral("2");
        table.addTransitionLiteral("3");
        table.addTransitionLiteral(".");
        for (int i = 0; i < 18; i++) {
            table.addState();
        }
        table.addTransition("a", 0, 1);
        table.addTransition("b", 1, 2);
        table.addTransition("b", 2, 2);
        table.addFreeTransition(1, 3);
        table.addFreeTransition(2, 3);
        table.addFreeTransition(3, 19);
        table.addTransition("c", 0, 4);
        table.addTransition("c", 0, 9);
        table.addTransition("c", 4, 4);
        table.addTransition("c", 9, 9);
        table.addFreeTransition(0, 10);
        table.addFreeTransition(9, 10);
        table.addFreeTransition(4, 5);
        table.addFreeTransition(5, 19);
        table.addTransition(".", 5, 6);
        table.addTransition(".", 10, 11);
        table.addTransition("c", 11, 12);
        table.addTransition("c", 12, 12);
        table.addTransition("c", 6, 7);
        table.addTransition("c", 7, 7);
        table.addTransition("c", 11, 12);
        table.addFreeTransition(6, 8);
        table.addFreeTransition(12, 13);
        table.addFreeTransition(7, 8);
        table.addFreeTransition(8, 19);
        table.addFreeTransition(13, 19);
        table.addTransition("0", 0, 14);
        table.addFreeTransition(14, 19);
        table.addTransition("1", 0, 15);
        table.addFreeTransition(15, 19);
        table.addTransition("2", 0, 16);
        table.addFreeTransition(16, 19);
        table.addTransition("3", 0, 17);
        table.addFreeTransition(17, 19);
        table.setFinalState(19);
        // set up expected table
        StateTable expected = new StateTable();
        expected.removeState(1);
        expected.removeState(0);
        expected.addTransitionLiteral("b");
        expected.addTransitionLiteral(SpecialTransitions.freeTransition);
        expected.addTransitionLiteral("a");
        expected.addTransitionLiteral("c");
        expected.addTransitionLiteral(".");
        expected.addTransitionLiteral("0");
        expected.addTransitionLiteral("1");
        expected.addTransitionLiteral("2");
        expected.addTransitionLiteral("3");
        for (int i = 0; i < 7; i++) {
            expected.addState();
        }
        expected.addTransition("a", 0, 1);
        expected.addTransition("c", 0, 3);
        expected.addTransition(".", 0, 6);
        expected.addTransition("0", 0, 5);
        expected.addTransition("1", 0, 5);
        expected.addTransition("2", 0, 5);
        expected.addTransition("3", 0, 5);
        expected.addTransition("b", 1, 1);
        expected.addTransition("c", 3, 3);
        expected.addTransition(".", 3, 4);
        expected.addTransition("c", 4, 4);
        expected.addTransition("c", 6, 4);
        expected.addFreeTransition(1, 2);
        expected.addFreeTransition(3, 2);
        expected.addFreeTransition(4, 2);
        expected.addFreeTransition(5, 2);
        expected.setFinalState(2);
        expected = DFAConverter.completeTable(expected);
        // set up actual table
        StateTable actual = new StateTable(DFAConverter.convertToDFA(table));
        assertEquals(expected, actual);
    }

    @Test
    public void mergeEquivalentTest() {
        // set up test table
        StateTable table = new StateTable();
        table.removeState(1);
        table.removeState(0);
        for (int i = 0; i < 11; i++) {
            table.addState();
        }
        table.addTransition("a", 0, 1);
        table.addTransition("b", 0, 2);
        table.addTransition("c", 0, 3);
        table.addTransition("d", 0, 4);
        table.addTransition("a", 1, 5);
        table.addTransition("a", 2, 5);
        table.addTransition("b", 3, 6);
        table.addTransition("b", 4, 6);
        table.addTransition("a", 5, 5);
        table.addTransition("b", 5, 7);
        table.addTransition("a", 6, 5);
        table.addTransition("b", 6, 7);
        table.addTransition("b", 7, 8);
        table.addTransition("a", 7, 9);
        table.addTransition("a", 8, 10);
        table.addTransition("a", 9, 10);
        table.setFinalState(10);
        // set up expected table
        StateTable expected = new StateTable();
        expected.removeState(1);
        expected.removeState(0);
        for (int i = 0; i < 6; i++) {
            expected.addState();
        }
        expected.addTransition("a", 0, 1);
        expected.addTransition("b", 0, 1);
        expected.addTransition("c", 0, 1);
        expected.addTransition("d", 0, 1);
        expected.addTransition("a", 1, 2);
        expected.addTransition("b", 1, 2);
        expected.addTransition("a", 2, 2);
        expected.addTransition("b", 2, 3);
        expected.addTransition("a", 3, 4);
        expected.addTransition("b", 3, 4);
        expected.addTransition("a", 4, 5);
        expected.setFinalState(5);
        // set up actual table
        StateTable actual = DFAConverter.mergeEquivalent(table);
        assertEquals(expected, actual);
    }

    @Test
    public void removeUnobtainableTest() {
        // set up test table
        StateTable table = new StateTable();
        table.removeState(1);
        table.removeState(0);
        for (int i = 0; i < 8; i++) {
            table.addState();
        }
        table.addTransition("a", 0, 2);
        table.addTransition("a", 1, 7);
        table.addTransition("a", 1, 4);
        table.addTransition("a", 1, 5);
        table.addTransition("a", 2, 5);
        table.addTransition("a", 3, 3);
        table.addTransition("a", 3, 6);
        table.addTransition("a", 4, 6);
        table.addTransition("a", 5, 5);
        table.addTransition("a", 5, 6);
        table.addTransition("a", 6, 7);
        table.setFinalState(7);
        // set up expected table
        StateTable expected = new StateTable();
        expected.removeState(1);
        expected.removeState(0);
        for (int i = 0; i < 5; i++) {
            expected.addState();
        }
        expected.addTransition("a", 0, 1);
        expected.addTransition("a", 1, 2);
        expected.addTransition("a", 2, 2);
        expected.addTransition("a", 2, 3);
        expected.addTransition("a", 3, 4);
        expected.setFinalState(4);
        // set up actual table
        StateTable actual = DFAConverter.removeUnobtainable(table);
        assertEquals(expected, actual);
    }

    @Test
    public void removeDeadEndsTest() {
        // set up test table
        StateTable table = new StateTable();
        table.removeState(1);
        table.removeState(0);
        for (int i = 0; i < 11; i++) {
            table.addState();
        }
        table.addTransition("a", 0, 1);
        table.addTransition("a", 0, 2);
        table.addTransition("a", 2, 4);
        table.addTransition("a", 2, 5);
        table.addTransition("a", 4, 4);
        table.addTransition("a", 4, 5);
        table.addTransition("a", 4, 7);
        table.addTransition("a", 5, 5);
        table.addTransition("a", 5, 9);
        table.addTransition("a", 6, 10);
        table.addTransition("a", 7, 10);
        table.addTransition("a", 8, 7);
        table.setFinalState(10);
        // set up expected table
        StateTable expected = new StateTable();
        expected.removeState(1);
        expected.removeState(0);
        for (int i = 0; i < 7; i++) {
            expected.addState();
        }
        expected.addTransition("a", 0, 1);
        expected.addTransition("a", 1, 2);
        expected.addTransition("a", 2, 2);
        expected.addTransition("a", 2, 4);
        expected.addTransition("a", 4, 6);
        expected.addTransition("a", 5, 4);
        expected.addTransition("a", 3, 6);
        expected.setFinalState(6);
        // set up actual table
        StateTable actual = DFAConverter.removeDeadEnds(table);
        assertEquals(expected, actual);
    }

    @Test
    public void eliminateNonDeterminismTest() {
        // set up test table
        StateTable table = new StateTable();
        table.addTransitionLiteral("a");
        table.addTransitionLiteral("b");
        table.addTransitionLiteral("c");
        table.addTransitionLiteral("0");
        table.addTransitionLiteral("1");
        table.addTransitionLiteral("2");
        table.addTransitionLiteral("3");
        table.addTransitionLiteral(".");
        for (int i = 0; i < 18; i++) {
            table.addState();
        }
        table.addTransition("a", 0, 1);
        table.addTransition("b", 1, 2);
        table.addTransition("b", 2, 2);
        table.addFreeTransition(1, 3);
        table.addFreeTransition(2, 3);
        table.addFreeTransition(3, 19);
        table.addTransition("c", 0, 4);
        table.addTransition("c", 0, 9);
        table.addTransition("c", 4, 4);
        table.addTransition("c", 9, 9);
        table.addFreeTransition(0, 10);
        table.addFreeTransition(9, 10);
        table.addFreeTransition(4, 5);
        table.addFreeTransition(5, 19);
        table.addTransition(".", 5, 6);
        table.addTransition(".", 10, 11);
        table.addTransition("c", 11, 12);
        table.addTransition("c", 12, 12);
        table.addTransition("c", 6, 7);
        table.addTransition("c", 7, 7);
        table.addTransition("c", 11, 12);
        table.addFreeTransition(6, 8);
        table.addFreeTransition(12, 13);
        table.addFreeTransition(7, 8);
        table.addFreeTransition(8, 19);
        table.addFreeTransition(13, 19);
        table.addTransition("0", 0, 14);
        table.addFreeTransition(14, 19);
        table.addTransition("1", 0, 15);
        table.addFreeTransition(15, 19);
        table.addTransition("2", 0, 16);
        table.addFreeTransition(16, 19);
        table.addTransition("3", 0, 17);
        table.addFreeTransition(17, 19);
        table.setFinalState(19);
        // set up expected table
        StateTable expectedTable = new StateTable();
        for (int i = 0; i < 11; i++) {
            expectedTable.addState();
        }
        expectedTable.addTransitionLiteral("b");
        expectedTable.addFreeTransition(2, 3);
        expectedTable.addTransitionLiteral("a");
        expectedTable.addTransitionLiteral("c");
        expectedTable.addTransitionLiteral(".");
        expectedTable.addTransitionLiteral("0");
        expectedTable.addTransitionLiteral("1");
        expectedTable.addTransitionLiteral("2");
        expectedTable.addTransitionLiteral("3");
        expectedTable.addTransition("a", 0, 1);
        expectedTable.addTransition("c", 0, 4);
        expectedTable.addTransition(".", 0, 11);
        expectedTable.addTransition("0", 0, 7);
        expectedTable.addTransition("1", 0, 8);
        expectedTable.addTransition("2", 0, 9);
        expectedTable.addTransition("3", 0, 10);
        expectedTable.addTransition("b", 1, 2);
        expectedTable.addFreeTransition(1, 3);
        expectedTable.addTransition("b", 2, 2);
        expectedTable.addTransition("c", 4, 4);
        expectedTable.addTransition(".", 4, 5);
        expectedTable.addFreeTransition(4, 3);
        expectedTable.addTransition("c", 5, 6);
        expectedTable.addFreeTransition(5, 3);
        expectedTable.addTransition("c", 6, 6);
        expectedTable.addFreeTransition(6, 3);
        expectedTable.addFreeTransition(7, 3);
        expectedTable.addFreeTransition(8, 3);
        expectedTable.addFreeTransition(9, 3);
        expectedTable.addFreeTransition(10, 3);
        expectedTable.addTransition("c", 11, 12);
        expectedTable.addTransition("c", 12, 12);
        expectedTable.addFreeTransition(12, 3);
        expectedTable.setFinalState(3);
        // set up actual table
        StateTable actualTable = DFAConverter.eliminateNonDeterminism(table);
        assertEquals(expectedTable, actualTable);
    }

    @Test
    public void buildEpsilonClosureTest() {
        // set up testTable
        StateTable testTable = new StateTable();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.setFinalState(testTable.addState());
        testTable.addTransitionLiteral("a");
        testTable.addTransitionLiteral("b");
        testTable.addTransition("a", 0, 4);
        testTable.addTransition("a", 0, 7);
        testTable.addTransition("a", 1, 5);
        testTable.addTransition("a", 3, 6);
        testTable.addTransition("b", 1, 7);
        testTable.addTransition("b", 2, 3);
        testTable.addFreeTransition(0, 1);
        testTable.addFreeTransition(0, 2);
        testTable.addFreeTransition(2, 4);
        testTable.addFreeTransition(2, 6);
        testTable.addFreeTransition(1, 2);
        // set up expectedList
        List<Integer> expectedList = new ArrayList<Integer>();
        expectedList.add(0);
        expectedList.add(1);
        expectedList.add(2);
        expectedList.add(4);
        expectedList.add(6);
        // set up actualList
        List<Integer> actualList = new ArrayList<Integer>();
        actualList = DFAConverter.buildEpsilonClosure(testTable, testTable.getStartState());

        assertEquals(expectedList, actualList);
    }
}