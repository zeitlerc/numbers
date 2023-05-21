package numbers;

public class Stats {
    private int newDuplicates;
    private int newUniques;
    private int totalUniques;

    public Stats(int newDuplicates, int newUniques, int totalUniques) {
        this.newDuplicates = newDuplicates;
        this.newUniques = newUniques;
        this.totalUniques = totalUniques;
    }

    public int getNewDuplicates() {
        return newDuplicates;
    }

    public int getNewUniques() {
        return newUniques;
    }

    public int getTotalUniques() {
        return totalUniques;
    }
}
