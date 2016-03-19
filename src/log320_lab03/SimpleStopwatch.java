package log320_lab03;

/**
 *
 * @author Zeldorine
 */
public class SimpleStopwatch {

    private long StartTime;
    public long Duration;

    public SimpleStopwatch() {
    }

    public void Start() {
        StartTime = System.currentTimeMillis();
    }

    public double Stop() {
        Duration = System.currentTimeMillis() - StartTime;

        return Duration;
    }
    
    public double getTime(){
        return System.currentTimeMillis() - StartTime;
    }

    public void Reset() {
        StartTime = 0;
        Duration = 0;
    }
}
