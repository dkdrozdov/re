package nfa;

import java.util.*;

public class StateTable {
    List<List<List<Integer>>> stateTable = null;
    List<String> inputLits = null;
    int startState;
    int finalState;

    public StateTable() {
        stateTable = new ArrayList<List<List<Integer>>>();
        inputLits = new ArrayList<String>();
        startState = addState();
        finalState = addState();
    }

    public int getFinalState() {
        return finalState;
    }

    public StateTable(StateTable table) {
        stateTable = new ArrayList<List<List<Integer>>>();
        inputLits = new ArrayList<String>();

        for (int row = 0; row < table.stateTable.size(); row++) {
            this.stateTable.add(new ArrayList<List<Integer>>());
            for (int transLit = 0; transLit < table.stateTable.get(row).size(); transLit++) {
                this.stateTable.get(row).add(new ArrayList<Integer>());
                for (int transition = 0; transition < table.stateTable.get(row).get(transLit).size(); transition++) {
                    this.stateTable.get(row).get(transLit).add(table.stateTable.get(row).get(transLit).get(transition));
                }
            }
        }
        table.inputLits.forEach(lit -> {
            this.inputLits.add(lit);
        });
        this.startState = table.getStartState();
        this.finalState = table.getFinalState();
    }

    public int getStartState() {
        return startState;
    }

    public void addTransition(String transitionLit, int fromState, int toState) {
        int indexOfTransition = inputLits.indexOf(transitionLit);
        stateTable.get(fromState).get(indexOfTransition).add(toState);
    }

    public int addFinalState(int fromState, String transitionLit) {
        int newState = addState();
        addTransition(transitionLit, fromState, newState);
        return newState;
    }

    public int addState() {
        // Add new row of ints in the bottom of the table
        stateTable.add(new ArrayList<List<Integer>>());
        for (int i = 0; i < this.inputLits.size(); i++) {
            stateTable.get(stateTable.size() - 1).add(new ArrayList<Integer>());
        }
        // Return index of created state
        return stateTable.size() - 1;
    }

    public void addInputLit(String newLit) {
        // Add new column of ints to the right of the table
        // (adds new element to each of states list elements)
        Iterator<List<List<Integer>>> stateTableIterator = stateTable.iterator();
        while (stateTableIterator.hasNext()) {
            stateTableIterator.next().add(new ArrayList<Integer>());
        }
        inputLits.add(newLit);
    }

    public void replaceStateOnlyTable(int oldIndex, int newIndex) {

        for (int row = 0; row < stateTable.size(); row++) {
            for (int transLit = 0; transLit < stateTable.get(row).size(); transLit++) {
                for (int transition = 0; transition < stateTable.get(row).get(transLit).size(); transition++) {
                    if (stateTable.get(row).get(transLit).get(transition) == oldIndex) {
                        stateTable.get(row).get(transLit).set(transition, newIndex);
                    }
                }
            }
        }
    }

    public void replaceStateIndex(int oldIndex, int newIndex) {
        replaceStateOnlyTable(oldIndex, newIndex);
        if (getStartState() == oldIndex) {
            startState = newIndex;
        }
        if (getFinalState() == oldIndex) {
            finalState = newIndex;
        }
    }

    void increaseStateIndexes(int n) {
        // increase each element in this.table
        for (int i = 0; i < stateTable.size(); i++) {
            for (int j = 0; j < stateTable.get(i).size(); j++) {
                for (int t = 0; t < stateTable.get(i).get(j).size(); t++) {
                    stateTable.get(i).get(j).set(t, stateTable.get(i).get(j).get(t) + n);
                }
            }
        }
        // increase startState
        startState += n;
        // increase finalState
        finalState += n;
    }

    private void swapColumns(int index1, int index2) {
        // swap in inputLits
        String tempLit = inputLits.get(index1);
        inputLits.set(index1, inputLits.get(index2));
        inputLits.set(index2, tempLit);

        // swap in table
        stateTable.forEach(row -> {
            List<Integer> tempTransition = row.get(index1);
            row.set(index1, row.get(index2));
            row.set(index2, tempTransition);
        });
    }

    public void concatenateStateTable(StateTable table) {
        // find new indexes for every state of table to avoid collisions
        StateTable correctedTable = new StateTable(table);
        correctedTable.increaseStateIndexes(this.stateTable.size());
        // Add every missing lit from correctedTable to this
        correctedTable.inputLits.forEach(lit -> {
            if (!this.inputLits.contains(lit)) {
                this.addInputLit(lit);
            }
        });
        // Add every missing lit from this to correctedTable
        this.inputLits.forEach(lit -> {
            if (!correctedTable.inputLits.contains(lit)) {
                correctedTable.addInputLit(lit);
            }
        });
        // sort literal array and stateTable of correctedTable to correspond
        // this.stateTable
        int lit = 0;
        while (lit < this.inputLits.size()) {
            while (!correctedTable.inputLits.get(lit).equals(this.inputLits.get(lit))) {
                correctedTable.swapColumns(lit, correctedTable.inputLits.indexOf(this.inputLits.get(lit)));
            }
            lit++;
        }
        /*
         * this.inputLits.forEach(lit -> { while
         * (correctedTable.inputLits.get(this.inputLits.indexOf(lit)) != lit) { int
         * litIndex = correctedTable.inputLits.indexOf(lit);
         * correctedTable.swapColumns(this.inputLits.indexOf(lit), litIndex); } });
         */
        // concatenate stateTable rows
        this.stateTable.addAll(correctedTable.stateTable);
        // this.startState remains
        // merge old finalState with old startState //
        int oldFinalState = this.finalState;
        this.finalState = correctedTable.getFinalState();
        mergeStates(oldFinalState, correctedTable.getStartState());
        // resolve avalible freeTransitions
        resolveFreeTransitions();
    }

    private void resolveFreeTransitions() {
        int freeTransitionLitIndex = inputLits.indexOf(SpecialTransitions.freeTransition);
        // check if there's even a free-transition literal in list
        if (freeTransitionLitIndex == -1) {
            return;
        }
        // find a free transition
        for (int row = 0; row < stateTable.size(); row++) {
            for (int transition = 0; transition < stateTable.get(row).get(freeTransitionLitIndex)
                    .size(); transition++) {
                // find all free transitions with same destination
                int destinationState = stateTable.get(row).get(freeTransitionLitIndex).get(transition);
                if (stateHasAnyTransitions(destinationState)) {
                    List<Integer> sameTransitionIndexes = new ArrayList<Integer>();
                    for (int transitionList = 0; transitionList < stateTable.size(); transitionList++) {
                        for (int freeTrans = 0; freeTrans < stateTable.get(row).get(freeTransitionLitIndex)
                                .size(); freeTrans++) {
                            if (stateTable.get(transitionList).get(freeTransitionLitIndex).contains(destinationState)) {
                                // add their index in the sameTransitionIndexes list
                                sameTransitionIndexes.add(transitionList);
                            }
                        }
                    }
                    // copy foresaid destination's transitions into found transition's states
                    for (int transitionIndex = 0; transitionIndex < sameTransitionIndexes.size(); transitionIndex++) {
                        copyTransitions(transitionIndex, destinationState);
                    }
                    // remove resolved free transitions
                    for (int transitionIndex = sameTransitionIndexes.size()
                            - 1; transitionIndex >= 0; transitionIndex--) {
                        stateTable.get(sameTransitionIndexes.get(transitionIndex)).get(freeTransitionLitIndex)
                                .remove(transition);
                    }
                    // remove destination state
                    removeState(destinationState);
                    sameTransitionIndexes.clear();
                }
            }
        }

    }

    public boolean stateHasAnyTransitions(int state) {
        for (int transLit = 0; transLit < stateTable.get(state).size(); transLit++) {
            if (stateTable.get(state).get(transLit).size() != 0) {
                return true;
            }
        }

        return false;
    }

    public void copyTransitions(int state1, int state2) {
        List<List<Integer>> rowState1 = stateTable.get(state1);
        List<List<Integer>> rowState2 = stateTable.get(state2);

        for (int transLit = 0; transLit < rowState2.size(); transLit++) {
            for (int transition = 0; transition < rowState2.get(transLit).size(); transition++) {
                if (!rowState1.get(transLit).contains(rowState2.get(transLit).get(transition))) {
                    rowState1.get(transLit).add(rowState2.get(transLit).get(transition));
                }
            }
        }
        /*
         * rowState2.forEach(transLit -> { transLit.forEach(transition -> { if
         * (!rowState1.get(rowState2.indexOf(transLit)).contains(transition)) {
         * rowState1.get(rowState2.indexOf(transLit)).add(transition); } }); });
         */
    }

    public void mergeStates(int state1, int state2) {
        copyTransitions(state1, state2);
        replaceStateIndex(state2, state1);
        removeState(state2);
    }

    public void setFinalState(int state) {
        finalState = state;
    }

    public void removeState(int state) {
        for (int s = state + 1; s < stateTable.size(); s++) {
            replaceStateIndex(s, s - 1);
        }
        stateTable.remove(state);
    }

    public void addFreeTransition(int fromState, int toState) {
        if (!inputLits.contains(SpecialTransitions.freeTransition)) {
            addInputLit(SpecialTransitions.freeTransition);
        }
        if (!stateTable.get(fromState).get(inputLits.indexOf(SpecialTransitions.freeTransition)).contains(toState)) {
            stateTable.get(fromState).get(inputLits.indexOf(SpecialTransitions.freeTransition)).add(toState);
        }
    }
}