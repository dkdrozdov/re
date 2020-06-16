package re.fa;

import java.util.*;

public class StateTable {
    public List<List<List<Integer>>> stateTable = null;
    public List<String> transitionLiterals = null;
    public List<Integer> metaFinalStates = null;
    public List<String> metaFinalStatesNames = null;
    public List<Integer> deleted = null;
    int startState;
    int finalState;
    int deadState;

    public List<Integer> getRelevantStates() {
        List<Integer> relevant = new ArrayList<Integer>();
        for (int i = 0; i < stateTable.size(); i++) {
            relevant.add(i);
        }
        relevant.removeAll(deleted);

        return relevant;
    }

    public StateTable() {
        metaFinalStatesNames = new ArrayList<String>();
        metaFinalStates = new ArrayList<Integer>();
        stateTable = new ArrayList<List<List<Integer>>>();
        transitionLiterals = new ArrayList<String>();
        deleted = new ArrayList<Integer>();
        startState = addState();
        finalState = addState();
    }

    public void addMetaFinalState(int state, String name) {
        if (!metaFinalStatesNames.contains(name)) {
            metaFinalStatesNames.add(name);
            metaFinalStates.add(state);
        } else {
            int duplicateIndex = metaFinalStatesNames.indexOf(name);
            mergeStates(duplicateIndex, state);
        }
    }

    public int getFinalState() {
        return finalState;
    }

    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            StateTable q = (StateTable) obj;
            return this.startState == q.startState && this.finalState == q.finalState && this.deadState == q.deadState
                    && this.stateTable.equals(q.stateTable) && this.transitionLiterals.equals(q.transitionLiterals);
        }
        return false;
    }

    public StateTable(StateTable table) {
        deleted = new ArrayList<Integer>();
        metaFinalStatesNames = new ArrayList<String>();
        metaFinalStates = new ArrayList<Integer>();
        stateTable = new ArrayList<List<List<Integer>>>();
        transitionLiterals = new ArrayList<String>();

        for (int row = 0; row < table.stateTable.size(); row++) {
            this.stateTable.add(new ArrayList<List<Integer>>());
            for (int transLit = 0; transLit < table.stateTable.get(row).size(); transLit++) {
                this.stateTable.get(row).add(new ArrayList<Integer>());
                for (int transition = 0; transition < table.stateTable.get(row).get(transLit).size(); transition++) {
                    this.stateTable.get(row).get(transLit).add(table.stateTable.get(row).get(transLit).get(transition));
                }
            }
        }
        for (int i = 0; i < table.deleted.size(); i++) {
            this.deleted.add(table.deleted.get(i));
        }
        for (int i = 0; i < table.metaFinalStates.size(); i++) {
            this.metaFinalStates.add(table.metaFinalStates.get(i));
            this.metaFinalStatesNames.add(table.metaFinalStatesNames.get(i));
        }
        table.transitionLiterals.forEach(lit -> {
            this.transitionLiterals.add(lit);
        });
        this.startState = table.getStartState();
        this.finalState = table.getFinalState();
        this.deadState = table.getDeadState();
    }

    public int getStartState() {
        return startState;
    }

    public void addTransition(String transitionLit, int fromState, int toState) {
        if (!transitionLiterals.contains(transitionLit)) {
            addTransitionLiteral(transitionLit);
        }
        int indexOfTransition = transitionLiterals.indexOf(transitionLit);
        stateTable.get(fromState).get(indexOfTransition).add(toState);
    }

    public int addFinalState(int fromState, String transitionLit) {
        int newState = addState();
        addTransition(transitionLit, fromState, newState);
        return newState;
    }

    public int addState() {
        // Add new row of ints in the bottom of the table
        if (deleted.size() != 0) {
            int state = deleted.get(0);
            deleted.remove(0);
            for (int transLit = 0; transLit < stateTable.get(state).size(); transLit++) {
                stateTable.get(state).get(transLit).clear();
            }
            return state;
        }
        stateTable.add(new ArrayList<List<Integer>>());
        for (int i = 0; i < this.transitionLiterals.size(); i++) {
            stateTable.get(stateTable.size() - 1).add(new ArrayList<Integer>());
        }
        // Return index of created state
        return stateTable.size() - 1;
    }

    public void addTransitionLiteral(String newLit) {
        // Add new column of ints to the right of the table
        // (adds new element to each of states list elements)
        Iterator<List<List<Integer>>> stateTableIterator = stateTable.iterator();
        while (stateTableIterator.hasNext()) {
            stateTableIterator.next().add(new ArrayList<Integer>());
        }
        transitionLiterals.add(newLit);
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
        if (metaFinalStates.contains(oldIndex)) {
            metaFinalStates.set(metaFinalStates.indexOf(oldIndex), newIndex);
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
        // increase metaFinals
        for (int i = 0; i < metaFinalStates.size(); i++) {
            metaFinalStates.set(i, metaFinalStates.get(i) + n);
        }
    }

    private void swapColumns(int index1, int index2) {
        // swap in inputLits
        String tempLit = transitionLiterals.get(index1);
        transitionLiterals.set(index1, transitionLiterals.get(index2));
        transitionLiterals.set(index2, tempLit);

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
        correctedTable.transitionLiterals.forEach(lit -> {
            if (!this.transitionLiterals.contains(lit)) {
                this.addTransitionLiteral(lit);
            }
        });
        // Add every missing lit from this to correctedTable
        this.transitionLiterals.forEach(lit -> {
            if (!correctedTable.transitionLiterals.contains(lit)) {
                correctedTable.addTransitionLiteral(lit);
            }
        });
        // sort literal array and stateTable of correctedTable to correspond
        // this.stateTable
        int lit = 0;
        while (lit < this.transitionLiterals.size()) {
            while (!correctedTable.transitionLiterals.get(lit).equals(this.transitionLiterals.get(lit))) {
                correctedTable.swapColumns(lit,
                        correctedTable.transitionLiterals.indexOf(this.transitionLiterals.get(lit)));
            }
            lit++;
        }
        // concatenate stateTable rows
        this.stateTable.addAll(correctedTable.stateTable);
        // this.startState remains
        // merge old finalState with old startState //
        int oldFinalState = this.finalState;
        this.finalState = correctedTable.getFinalState();
        mergeStates(oldFinalState, correctedTable.getStartState());
        // resolve avalible freeTransitions
        // resolveFreeTransitions();
    }

    public void resolveFreeTransitions() {
        int freeTransitionLitIndex = transitionLiterals.indexOf(SpecialTransitions.freeTransition);
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
    }

    public boolean isFinal(int state) {
        if (state == finalState) {
            return true;
        }
        if (metaFinalStates.contains(state)) {
            return true;
        }
        return false;
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
        if (isDeleted(state)) {
            throw new Error("State is already removed.");
        }
        removeTransitionsToState(state);
        deleted.add(state);
        if (metaFinalStates.indexOf(state) != -1) {
            metaFinalStatesNames.remove(metaFinalStates.indexOf(state));
            metaFinalStates.remove(metaFinalStates.indexOf(state));
        }
    }

    public void addFreeTransition(int fromState, int toState) {
        if (!transitionLiterals.contains(SpecialTransitions.freeTransition)) {
            addTransitionLiteral(SpecialTransitions.freeTransition);
        }
        if (!stateTable.get(fromState).get(transitionLiterals.indexOf(SpecialTransitions.freeTransition))
                .contains(toState)) {
            stateTable.get(fromState).get(transitionLiterals.indexOf(SpecialTransitions.freeTransition)).add(toState);
        }
    }

    public void removeTransitionsToState(int state) {
        for (int row = 0; row < stateTable.size(); row++) {
            for (int transLit = 0; transLit < stateTable.get(row).size(); transLit++) {
                for (int transition = stateTable.get(row).get(transLit).size() - 1; transition >= 0; transition--) {
                    if (stateTable.get(row).get(transLit).get(transition) == state) {
                        stateTable.get(row).get(transLit).remove(transition);
                    }
                }
            }
        }
    }

    public void copyTransitions(int state1, int state2, StateTable table) {
        // List<List<Integer>> rowState1 = stateTable.get(state1);
        List<List<Integer>> rowState2 = table.stateTable.get(state2);

        for (int transLit = 0; transLit < rowState2.size(); transLit++) {
            for (int transition = 0; transition < rowState2.get(transLit).size(); transition++) {
                // if
                // (!rowState1.get(transLit).contains(rowState2.get(transLit).get(transition)))
                // {
                addTransition(table.transitionLiterals.get(transLit), state1, rowState2.get(transLit).get(transition));
                // rowState1.get(transLit).add(rowState2.get(transLit).get(transition));
                // }
            }
        }
    }

    public List<Integer> stateDestinations(int state) {
        List<Integer> destinations = new ArrayList<Integer>();
        for (int transLit = 0; transLit < stateTable.get(state).size(); transLit++) {
            for (int transition = 0; transition < stateTable.get(state).get(transLit).size(); transition++) {
                int currentTransition = stateTable.get(state).get(transLit).get(transition);
                if (!destinations.contains(currentTransition)) {
                    destinations.add(currentTransition);
                }
            }
        }

        return destinations;
    }

    public void setDeadState(int state) {
        deadState = state;
    }

    public int getDeadState() {
        return deadState;
    }

    public String getMetaFinalName(int state) {
        return metaFinalStatesNames.get(metaFinalStates.indexOf(state));
    }

    public boolean isDeleted(Integer state) {
        return deleted.contains(state);
    }

    public int getTransition(int state, int transLit, int transition) {
        if (isDeleted(state) || isDeleted(stateTable.get(state).get(transLit).get(transition))) {
            throw new Error("State is deleted.");
        }
        return stateTable.get(state).get(transLit).get(transition);
    }
}