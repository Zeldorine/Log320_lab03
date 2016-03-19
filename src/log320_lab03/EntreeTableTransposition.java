package log320_lab03;

/**
 *
 * @author Zeldorine
 */
public class EntreeTableTransposition {
    public enum BoardTTEntryType {
        ExactValue,
        Lowerbound,
        Upperbound
    }

    public EntreeTableTransposition(int BoardValue, BoardTTEntryType EntryType, int SearchDepth) {
        Value = BoardValue;
        Type = EntryType;
        Depth = SearchDepth;
    }

    public int Value;
    public BoardTTEntryType Type;
    public int Depth;
}
