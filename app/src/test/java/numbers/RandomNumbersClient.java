package numbers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomNumbersClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RandomNumbersClient.class);
    private String host;
    private int port;
    private int countNumbers;

    /**
     * Sends random 9 digit numbers to the socket then closes the connection
     * @param host
     * @param port
     * @param countNumbers The amount of random numbers to send. Example, send 2000 numbers then close the connection
     */
    public RandomNumbersClient(String host, int port, int countNumbers) {
        this.host = host;
        this.port = port;
        this.countNumbers = countNumbers;
    }

    @Override
    public void run() {
        Random random = new Random();
        try(Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            logger.debug("RandomNumbersClient connected to socket.");
            
            for(int i = 0; i < countNumbers && !Thread.currentThread().isInterrupted(); i++) {
                int randomNumber = random.nextInt(1000000000);
                out.println(String.format("%09d", randomNumber));
            }
            logger.debug("RandomNumbersClient is done writing numbers.");
        } catch(Exception e) {
            logger.error("Could not write to the socket.", e);
        }
    }

    /**
     * Used for manual testing
     */
    public static void main(String[] args) {
        int totalCountNumbers = 2000000000;
        int countClients = 5;
        int clientCountNumbers = Math.floorDiv(totalCountNumbers, countClients);
        ExecutorService executor = Executors.newCachedThreadPool();
        for(int i = 0; i < countClients; i++) {
            executor.submit(new RandomNumbersClient("localhost", App.PORT, clientCountNumbers));
        }
        try {
            executor.shutdown();
            executor.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("RandomNumbersClient main was interrupted", e);
        }
    }
}
