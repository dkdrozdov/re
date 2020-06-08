package nfa;

import java.util.*;

public class StateTable {
    List<List<List<Integer>>> stateTable = null;
    List<List<Integer>> freeTransitions = null;
    List<Character> inputLits = null;
    public List<Integer> states = null;
    int startState;
    int finalState;

    public StateTable() {
        stateTable = new ArrayList<List<List<Integer>>>();
        freeTransitions = new ArrayList<List<Integer>>();
        inputLits = new ArrayList<Character>();
        states = new ArrayList<Integer>();
        startState = addState();
        finalState = addState();
    }

    public int getFinalState() {
        return finalState;
    }

    public StateTable(StateTable table) {
        stateTable = new ArrayList<List<List<Integer>>>();
        freeTransitions = new ArrayList<List<Integer>>();
        inputLits = new ArrayList<Character>();
        states = new ArrayList<Integer>();

        for (int row = 0; row < table.stateTable.size(); row++) {
            this.stateTable.add(new ArrayList<List<Integer>>());
            for (int transLit = 0; transLit < table.stateTable.get(row).size(); transLit++) {
                this.stateTable.get(row).add(new ArrayList<Integer>());
                for (int transition = 0; transition < table.stateTable.get(row).get(transLit).size(); transition++) {
                    this.stateTable.get(row).get(transLit).add(table.stateTable.get(row).get(transLit).get(transition));
                }
            }
        }
        /*
         * table.stateTable.forEach(row -> { this.stateTable.add(new
         * ArrayList<List<Integer>>()); int indexOfRow = table.stateTable.indexOf(row);
         * // List<List<Integer>> currentRow = this.stateTable.get(indexOfRow);
         * row.forEach(transLit -> { this.stateTable.get(indexOfRow).add(new
         * ArrayList<Integer>()); int indexOfTranslit =
         * table.stateTable.get(indexOfRow).indexOf(transLit);
         * transLit.forEach(transition -> {
         * this.stateTable.get(indexOfRow).get(indexOfTranslit).add(transition); }); //
         * currentRow.set(indexOfTranslit, ); }); });
         */
        for (int row = 0; row < table.freeTransitions.size(); row++) {
            this.freeTransitions.add(new ArrayList<Integer>());
            for (int transition = 0; transition < table.freeTransitions.get(row).size(); transition++) {
                this.freeTransitions.get(row).add(table.freeTransitions.get(row).get(transition));
            }

        }
        /*
         * table.freeTransitions.forEach(row -> { this.freeTransitions.add(new
         * ArrayList<Integer>()); int indexOfRow = table.freeTransitions.indexOf(row);
         * // List<List<Integer>> currentRow = this.freeTransitions.get(indexOfRow);
         * row.forEach(transition -> {
         * this.freeTransitions.get(indexOfRow).add(transition); //
         * currentRow.set(indexOfTranslit, ); }); });
         */
        table.inputLits.forEach(lit -> {
            this.inputLits.add(lit);
        });
        table.states.forEach(state -> {
            this.states.add(state);
        });
        this.startState = table.getStartState();
        this.finalState = table.getFinalState();
    }

    public int getStartState() {
        return startState;
    }

    int findMaxIndexState() {
        int stateIndex = 0;
        Iterator<Integer> statesIterator = states.iterator();
        int currentStateIndex = 0;
        while (statesIterator.hasNext()) {
            currentStateIndex = statesIterator.next();
            if (currentStateIndex > stateIndex) {
                stateIndex = currentStateIndex;
            }
        }

        return stateIndex;
    }

    public int generateStateIndex() {
        if (states.size() == 0) {
            return 0;
        }
        return findMaxIndexState() + 1;
    }

    public void addTransition(char transitionLit, int fromState, int toState) {
        int indexOfRow = this.states.indexOf(fromState);
        int indexOfTransition = this.inputLits.indexOf(transitionLit);
        /*
         * if (stateTable.get(indexOfRow).get(indexOfTransition).contains(-1)) {
         * stateTable.get(indexOfRow).get(indexOfTransition).remove(-1); }
         */
        stateTable.get(indexOfRow).get(indexOfTransition).add(toState);
        // stateTable.get(states.indexOf(fromState)).set(inputLits.indexOf(transitionLit),
        // toState);
    }

    public int addFinalState(int fromState, char transitionLit) {
        int newState = addState();
        addTransition(transitionLit, fromState, newState);
        return newState;
    }

    public int addState() {
        // Add new row of ints in the bottom of the table
        stateTable.add(new ArrayList<List<Integer>>());
        freeTransitions.add(new ArrayList<Integer>());
        states.add(generateStateIndex());
        for (int i = 0; i < this.inputLits.size(); i++) {
            stateTable.get(states.size() - 1).add(new ArrayList<Integer>());
        }
        // Return index of created state
        return states.get(states.size() - 1);
    }

    public int addState(int state) {
        // Add new row of ints in the bottom of the table
        stateTable.add(new ArrayList<List<Integer>>());
        freeTransitions.add(new ArrayList<Integer>());
        states.add(state);
        for (int i = 0; i < this.inputLits.size(); i++) {
            stateTable.get(states.size() - 1).add(new ArrayList<Integer>());
        }
        // Return index of created state
        return states.get(states.size() - 1);
    }

    public void addInputLit(char newLit) {
        // Add new column of ints to the right of the table
        // (adds new element to each of states list elements)
        Iterator<List<List<Integer>>> stateTableIterator = stateTable.iterator();
        while (stateTableIterator.hasNext()) {
            stateTableIterator.next().add(new ArrayList<Integer>());
        }
        inputLits.add(newLit);
    }

    public void replaceStateOnlyTable(int oldIndex, int newIndex) {
        stateTable.forEach(row -> {
            row.forEach(transLit -> {
                transLit.forEach(transition -> {
                    if (transition == oldIndex) {
                        stateTable.get(stateTable.indexOf(row)).get(row.indexOf(transLit))
                                .set(transLit.indexOf(transition), newIndex);
                    }
                });
            });
        });
        freeTransitions.forEach(row -> {
            row.forEach(transition -> {
                if (transition == oldIndex) {
                    freeTransitions.get(freeTransitions.indexOf(row)).set(row.indexOf(transition), newIndex);
                }
            });
        });
        /*
         * Iterator<List<List<Integer>>> tableRowIterator = stateTable.iterator();
         * Iterator<List<Integer>> tableColumnIterator = null; List<Integer>
         * currentElement; List<List<Integer>> currentRow; while
         * (tableRowIterator.hasNext()) { currentRow = tableRowIterator.next();
         * tableColumnIterator = currentRow.iterator(); while
         * (tableColumnIterator.hasNext()) { currentElement =
         * tableColumnIterator.next(); currentElement.forEach(transition -> { if
         * (transition == oldIndex) {
         * currentElement.set(currentElement.indexOf(transition), newIndex); } }); } }
         */
    }

    public void replaceStateIndex(int oldIndex, int newIndex) {
        states.set(states.indexOf(oldIndex), newIndex);
        replaceStateOnlyTable(oldIndex, newIndex);
    }

    void increaseStateIndexes(int n) {
        // increase each state in this.states
        for (int i = 0; i < states.size(); i++) {
            states.set(i, states.get(i) + n);
        }
        // increase each element in this.table
        for (int i = 0; i < stateTable.size(); i++) {
            for (int j = 0; j < stateTable.get(i).size(); j++) {
                for (int t = 0; t < stateTable.get(i).get(j).size(); t++) {
                    stateTable.get(i).get(j).set(t, stateTable.get(i).get(j).get(t) + n);
                }
            }
        }
        // increase each element in this.freeTransitions
        for (int i = 0; i < freeTransitions.size(); i++) {
            for (int j = 0; j < freeTransitions.get(i).size(); j++) {
                freeTransitions.get(i).set(j, freeTransitions.get(i).get(j) + n);
            }
        }
        // increase startState
        startState += n;
        // increase finalState
        finalState += n;
    }

    private void swapColumns(int index1, int index2) {
        // swap in inputLits
        char tempLit = this.inputLits.get(index1);
        this.inputLits.set(index1, this.inputLits.get(index2));
        this.inputLits.set(index2, tempLit);

        // swap in table
        this.stateTable.forEach(row -> {
            List<Integer> tempTransition = row.get(index1);
            row.set(index1, row.get(index2));
            row.set(index2, tempTransition);
        });
    }

    public void concatenateStateTable(StateTable table) {
        // find new indexes for every state of table to avoid collisions
        StateTable correctedTable = new StateTable(table);
        correctedTable.increaseStateIndexes(this.findMaxIndexState() + 1);
        // Add every missing lit from correctedTable to this
        correctedTable.inputLits.forEach(lit -> {
            if (this.inputLits.indexOf(lit) == -1) {
                this.addInputLit(lit);
            }
        });
        // Add every missing lit from this to correctedTable
        this.inputLits.forEach(lit -> {
            if (correctedTable.inputLits.indexOf(lit) == -1) {
                correctedTable.addInputLit(lit);
            }
        });
        // sort literal array and stateTable of correctedTable to correspond
        // this.stateTable
        this.inputLits.forEach(lit -> {
            while (correctedTable.inputLits.get(this.inputLits.indexOf(lit)) != lit) {
                int litIndex = correctedTable.inputLits.indexOf(lit);
                /*
                 * if (litIndex == -1) { correctedTable.addInputLit(lit); litIndex =
                 * correctedTable.inputLits.indexOf(lit); }
                 */
                correctedTable.swapColumns(this.inputLits.indexOf(lit), litIndex);
            }
        });
        // concatenate stateTable rows
        this.stateTable.addAll(correctedTable.stateTable);
        this.freeTransitions.addAll(correctedTable.freeTransitions);
        // concatenate states
        this.states.addAll(correctedTable.states);
        // this.startState remains
        // merge old finalState with old startState //
        int oldFinalState = this.finalState;
        this.finalState = correctedTable.getFinalState();
        mergeStates(oldFinalState, correctedTable.getStartState());
        // resolve avalible freeTransitions
        resolveFreeTransitions();
    }

    private void resolveFreeTransitions() {
        // find a free transition
        freeTransitions.forEach(row -> {
            row.forEach(transition -> {
                // find all free transitions with same destination
                List<Integer> sameTransitionIndexes = new ArrayList<Integer>();
                freeTransitions.forEach(transitionList -> {
                    if (transitionList.contains(transition)) {
                        // add their index in the sameTransitionIndexes list
                        sameTransitionIndexes.add(freeTransitions.indexOf(transitionList));
                    }
                });
                // copy foresaid destination's transitions into found transition's
                sameTransitionIndexes.forEach(transitionIndex -> {
                    copyTransitions(states.indexOf(transitionIndex), transition);
                });
            });
        });
    }

    public void copyTransitions(int state1, int state2) {
        int indexOfState1 = this.states.indexOf(state1);
        List<List<Integer>> rowState1 = this.stateTable.get(indexOfState1);
        int indexOfState2 = this.states.indexOf(state2);
        List<List<Integer>> rowState2 = this.stateTable.get(indexOfState2);

        rowState2.forEach(transLit -> {
            transLit.forEach(transition -> {
                if (!rowState1.get(rowState2.indexOf(transLit)).contains(transition)) {
                    rowState1.get(rowState2.indexOf(transLit)).add(transition);
                }
            });
        });

    }

    public void copyFreeTransitions(int state1, int state2) {
        int indexOfState1 = this.states.indexOf(state1);
        int indexOfState2 = this.states.indexOf(state2);
        freeTransitions.get(indexOfState2).forEach(transition -> {
            if (!freeTransitions.get(indexOfState1).contains(transition)) {
                freeTransitions.get(indexOfState1).add(transition);
            }
        });
    }

    public void mergeStates(int state1, int state2) {
        copyTransitions(state1, state2);
        copyFreeTransitions(state1, state2);
        removeState(state2);
        replaceStateOnlyTable(state2, state1);
        // carry over start/final state status
        if (startState == state2) {
            startState = state1;
        }
        if (finalState == state2) {
            finalState = state1;
        }
    }

    public void setFinalState(int state) {
        finalState = state;
    }

    public void removeState(int state) {
        this.stateTable.remove(states.indexOf(state));
        this.freeTransitions.remove(states.indexOf(state));
        this.states.remove(state);
    }

    public void addFreeTransition(int State1, int State2) {
        freeTransitions.get(states.indexOf(State1)).add(State2);
    }
}