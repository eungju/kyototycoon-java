package kyototycoon.benchmark;

public class StopWatch {
    private long startedAt;
    private long stoppedAt;

    public StopWatch start() {
        startedAt = System.currentTimeMillis();
        return this;
    }

    public StopWatch stop() {
        stoppedAt = System.currentTimeMillis();
        return this;
    }

    public long elapsed() {
        return stoppedAt - startedAt;
    }
}
