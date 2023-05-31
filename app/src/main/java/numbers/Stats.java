package numbers;

import java.util.List;
import java.util.Map.Entry;

public class Stats {
    private int newDuplicates;
    private int newUniques;
    private int totalUniques;
    private List<Entry<String, Integer>> topDuplicates;

    public Stats(int newDuplicates, int newUniques, int totalUniques, List<Entry<String, Integer>> topDuplicates) {
        this.newDuplicates = newDuplicates;
        this.newUniques = newUniques;
        this.totalUniques = totalUniques;
        this.topDuplicates = topDuplicates;
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

    public List<Entry<String, Integer>> getTopDuplicates() {
        return topDuplicates;
    }
}
