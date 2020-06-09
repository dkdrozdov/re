package nfa;

import java.util.*;

public class StateTable {
    List<List<List<Integer>>> stateTable = null;
    List<List<Integer>> freeTransitions = null;
    List<Character> inputLits = null;
    int startState;
    int finalState;

    public StateTable() {
        stateTable = new ArrayList<List<List<Integer>>>();
        freeTransitions = new ArrayList<List<Integer>>();
        inputLits = new ArrayList<Character>();
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

        for (int row = 0; row < table.stateTable.size(); row++) {
            this.stateTable.add(new ArrayList<List<Integer>>());
            for (int transLit = 0; transLit < table.stateTable.get(row).size(); transLit++) {
                this.stateTable.get(row).add(new ArrayList<Integer>());
                for (int transition = 0; transition < table.stateTable.get(row).get(transLit).size(); transition++) {
                    this.stateTable.get(row).get(transLit).add(table.stateTable.get(row).get(transLit).get(transition));
                }
            }
        }
        for (int row = 0; row < table.freeTransitions.size(); row++) {
            this.freeTransitions.add(new ArrayList<Integer>());
            for (int transition = 0; transition < table.freeTransitions.get(row).size(); transition++) {
                this.freeTransitions.get(row).add(table.freeTransitions.get(row).get(transition));
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

    public void addTransition(char transitionLit, int fromState, int toState) {
        int indexOfTransition = inputLits.indexOf(transitionLit);
        stateTable.get(fromState).get(indexOfTransition).add(toState);
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
        for (int i = 0; i < this.inputLits.size(); i++) {
            stateTable.get(stateTable.size() - 1).add(new ArrayList<Integer>());
        }
        // Return index of created state
        return stateTable.size() - 1;
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

        for (int row = 0; row < stateTable.size(); row++) {
            for (int transLit = 0; transLit < stateTable.get(row).size(); transLit++) {
                for (int transition = 0; transition < stateTable.get(row).get(transLit).size(); transition++) {
                    if (stateTable.get(row).get(transLit).get(transition) == oldIndex) {
                        stateTable.get(row).get(transLit).set(transition, newIndex);
                    }
                }
            }
        }
        for (int row = 0; row < freeTransitions.size(); row++) {
            for (int transition = 0; transition < freeTransitions.get(row).size(); transition++) {
                if (freeTransitions.get(row).get(transition) == oldIndex) {
                    freeTransitions.get(row).set(transition, newIndex);
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
            while (correctedTable.inputLits.get(lit) != this.inputLits.get(lit)) {
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
        this.freeTransitions.addAll(correctedTable.freeTransitions);
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
        for (int row = 0; row < freeTransitions.size(); row++) {
            for (int transition = 0; transition < freeTransitions.get(row).size(); transition++) {
                // find all free transitions with same destination
                int destinationState = freeTransitions.get(row).get(transition);
                if (stateHasAnyTransitions(destinationState)) {
                    List<Integer> sameTransitionIndexes = new ArrayList<Integer>();
                    for (int transitionList = 0; transitionList < freeTransitions.size(); transitionList++) {
                        if (freeTransitions.get(transitionList).contains(destinationState)) {
                            // add their index in the sameTransitionIndexes list
                            sameTransitionIndexes.add(transitionList);
                        }
                    }
                    // copy foresaid destination's transitions into found transition's states
                    for (int transitionIndex = 0; transitionIndex < sameTransitionIndexes.size(); transitionIndex++) {
                        copyTransitions(transitionIndex, destinationState);
                    }
                    // remove resolved free transitions
                    for (int transitionIndex = sameTransitionIndexes.size()
                            - 1; transitionIndex >= 0; transitionIndex--) {
                        freeTransitions.get(sameTransitionIndexes.get(transitionIndex)).remove(transition);
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

    public void copyFreeTransitions(int state1, int state2) {
        freeTransitions.get(state2).forEach(transition -> {
            if (!freeTransitions.get(state1).contains(transition)) {
                freeTransitions.get(state1).add(transition);
            }
        });
    }

    public void mergeStates(int state1, int state2) {
        copyTransitions(state1, state2);
        copyFreeTransitions(state1, state2);
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
        freeTransitions.remove(state);
    }

    public void addFreeTransition(int state1, int state2) {
        freeTransitions.get(state1).add(state2);
    }
}