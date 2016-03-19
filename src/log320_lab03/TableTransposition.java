package log320_lab03;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zeldorine
 */
public class TableTransposition {
    public static long[] PRNArray;
    public static long Seed = 1;
    public static Map<Long, EntreeTableTransposition> Table;
    public static TableTransposition instance = new TableTransposition();

    public TableTransposition() {
        PRNArray = new long[129];
        MTRandom MT = new MTRandom(Seed);
        for (int i = 0; i < PRNArray.length; i++) {
            PRNArray[i] = MT.nextInt();
        }

        Table = new HashMap<Long, EntreeTableTransposition>();
    }

    public static void sauveEntree(long Key, EntreeTableTransposition Entry) {
        if (!Table.containsKey(Key)) {
            Table.put(Key, Entry);
        }
    }
}
