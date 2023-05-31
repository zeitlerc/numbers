package numbers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberListener implements Callable<ListenerResult> {
    private static final Logger logger = LoggerFactory.getLogger(NumberListener.class);
    private ServerSocket server;
    private NumberTracker numberTracker;
    private NumberFileWriter fileWriter;

    private static final Pattern numberRegex = Pattern.compile("^[0-9]{9}$");

    public NumberListener(ServerSocket server, NumberTracker numberTracker, NumberFileWriter fileWriter) {
        this.server = server;
        this.numberTracker = numberTracker;
        this.fileWriter = fileWriter;
    }

    @Override
    public ListenerResult call() throws Exception {
        String line;
        BufferedReader in = null;
        try {
            Socket client = server.accept();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            logger.error("Could not accept the connection", e);
            return ListenerResult.CLOSED;
        }
        logger.info("Client has connected");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = in.readLine();
                
                if(line == null) {
                    return ListenerResult.CLOSED;
                } else if(line.equals("terminate")) {
                    return ListenerResult.TERMINATED;
                } else if(!isValidNumber(line)) {
                    logger.warn(line + " was not a valid number.  Connection terminated");
                    return ListenerResult.CLOSED;
                }

                boolean isDuplicate = numberTracker.trackDuplicate(line);
                if(!isDuplicate) {
                    fileWriter.write(line);
                }
            } catch (IOException e) {
                logger.error("Could not read from connection", e);
                return ListenerResult.CLOSED;
            }
        }
        return ListenerResult.CLOSED;
    }

    /**
     * @param value
     * @return True if value is a 9 digit number otherwise false
     */
    private boolean isValidNumber(String value) {
        if(value == null) {
            return false;
        }
        Matcher matcher = numberRegex.matcher(value);
        return matcher.find();
    }
}
