package re.fa;

public class StateIndex {
    private int state = -1;

    public void setState(int s) {
        state = s;
    }

    public StateIndex(int index) {
        state = index;
    }

    public int getState() {
        return state;
    }
}