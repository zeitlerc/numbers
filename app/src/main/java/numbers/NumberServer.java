package numbers;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NumberServer implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(NumberServer.class);
    private NumberListenerFactory listenerFactory;
    private ServerSocket server;
    private ThreadPoolExecutor listenerExecutor;
    private CompletionService<ListenerResult> listenerCompletion;

    public NumberServer(int numConnections, NumberListenerFactory listenerFactory) {
        this.listenerFactory = listenerFactory;
        this.listenerExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(numConnections);
        this.listenerCompletion = new ExecutorCompletionService<>(this.listenerExecutor);
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            listenerCompletion.submit(listenerFactory.getInstance());
            if(listenerExecutor.getActiveCount() >= listenerExecutor.getCorePoolSize()) {
                // Wait for one of the running connections to terminate before creating a new one
                try {
                    ListenerResult result = listenerCompletion.take().get();
                    if(result.equals(ListenerResult.TERMINATED)) {
                        logger.info("A client sent terminate");
                        return;
                    } else {
                        logger.debug("A client closed its connection");
                    }
                } catch(InterruptedException e) {
                    // The entire program is being terminated
                    logger.info("NumberServer was interrupted");
                    return;
                } catch(ExecutionException e) {
                    // The existing client would be terminated and a new one should be added to the pool
                    logger.info("NumberListener threw an exception", e);
                }
            }
        }
    }

    public void close() {
        if(listenerExecutor != null) {
            listenerExecutor.shutdown();
            try {
                listenerExecutor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("listenerExecutor was interrupted while awaiting shutdown", e);
            }
        }
        try {
            if(server != null) {
                server.close();
            }
        } catch (IOException e) {
            logger.info("Could not close socket", e);
        }
    }
}
