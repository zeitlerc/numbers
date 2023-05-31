package numbers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class NumberTracker {
    private Map<String, Integer> numbers = new HashMap<>(1000000);
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
        boolean isDuplicate = numbers.containsKey(number);
        if(isDuplicate) {
            numbers.put(number, numbers.get(number) + 1);
            totalDuplicates++;
        } else {
            numbers.put(number, 1);
        }
        return isDuplicate;
    }

    public synchronized Stats resetStats() {
        /* Making sure to have a copy of numbers.size() and totalDuplicates since they could be changed
        *  by another thread as this method runs resulting in inconsisent numbers between reports.
        *  Java optimizations might coincidentally avoid that issue, but I don't want to rely on coincidence. */
        int totalUniques = numbers.size();
        int copyTotalDuplicates = totalDuplicates;
        /* This threw NegativeArraySizeException in sorted and the reporting task did not catch/log
         * the exception so the failure was not obvious.  Stream isn't safe to use with multithreaded access
         * so this entire method (or at least the topDuplicate stream parts) needed to become synchronized.
         * Sorting the entire map is not efficient, but it still performs well enough here.
         */
        List<Entry<String, Integer>> topDuplicates = numbers.entrySet().stream()
            .sorted(Comparator.comparingInt(Entry<String, Integer>::getValue).reversed())
            .limit(10)
            .collect(Collectors.toList());

        Stats result = new Stats(copyTotalDuplicates - oldDuplicates, totalUniques - oldUniques, totalUniques, topDuplicates);
        oldUniques = totalUniques;
        oldDuplicates = copyTotalDuplicates;
        return result;
    }
}