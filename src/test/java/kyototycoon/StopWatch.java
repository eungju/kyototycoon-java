package kyototycoon;

public class StopWatch {
    private long startedAt;
    private long stoppedAt;

    public void start() {
        startedAt = System.currentTimeMillis();    
    }

    public void stop() {
        stoppedAt = System.currentTimeMillis();
    }

    public long elapsed() {
        return stoppedAt - startedAt;
    }
}
