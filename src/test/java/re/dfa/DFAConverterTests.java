package re.dfa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import re.fa.StateTable;

public class DFAConverterTests {
    @Test
    public void buildEpsilonClosureTest() {
        // set up testTable
        StateTable testTable = new StateTable();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.addState();
        testTable.setFinalState(testTable.stateTable.size() - 1);
        testTable.addInputLit("a");
        testTable.addInputLit("b");
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