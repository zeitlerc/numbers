package numbers;

import java.net.ServerSocket;

public class NumberListenerFactory {
    private ServerSocket server;
    private NumberTracker tracker;
    private NumberFileWriter fileWriter;
    public NumberListenerFactory(ServerSocket server, NumberTracker tracker, NumberFileWriter fileWriter) {
        this.server = server;
        this.tracker = tracker;
        this.fileWriter = fileWriter;
    }

    public NumberListener getInstance() {
        return new NumberListener(server, tracker, fileWriter);
    }
}
