package numbers;

import java.util.HashSet;
import java.util.Set;

public class NumberTracker {
    private Set<String> numbers = new HashSet<>(1000000);
    private int oldUniques = 0;
    private int oldDuplicates = 0;
    private int totalDuplicates = 0;

    public NumberTracker() {
    }

    /**
     * Checks if the number was a duplicate and adds it to the duplicate tracker.
     * @param number
     * @return true if this number was a duplicate
     */
    public synchronized boolean trackDuplicate(String number) {
        boolean isDuplicate = !numbers.add(number);
        if(isDuplicate) {
            totalDuplicates++;
        }
        return isDuplicate;
    }

    public Stats resetStats() {
        /* Making sure to have a copy of numbers.size() and totalDuplicates since they could be changed
        *  by another thread as this method runs resulting in inconsisent numbers between reports.
        *  Java optimizations might coincidentally avoid that issue, but I don't want to rely on coincidence. */
        int totalUniques = numbers.size();
        int copyTotalDuplicates = totalDuplicates;

        Stats result = new Stats(copyTotalDuplicates - oldDuplicates, totalUniques - oldUniques, totalUniques);
        oldUniques = totalUniques;
        oldDuplicates = copyTotalDuplicates;
        return result;
    }
}