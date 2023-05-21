package numbers;

import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminatingClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TerminatingClient.class);
    private String host;
    private int port;

    /**
     * Sends only the terminate command to the server
     * @param host
     * @param port
     */
    public TerminatingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try(Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("terminate");
        } catch(Exception e) {
            logger.error("Could not write to the socket.", e);
        }
    }

    /**
     * Used for manual testing
     */
    public static void main(String[] args) {
        new TerminatingClient("localhost", App.PORT).run();
    }
}
