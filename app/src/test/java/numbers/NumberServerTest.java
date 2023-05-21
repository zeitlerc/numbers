package numbers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class NumberServerTest {
    private final class NumberListenerExtension extends NumberListener {
        private boolean sleeps;
        private ListenerResult result;

        private NumberListenerExtension(boolean sleeps, ListenerResult result) {
            super(null, null, null);
            this.sleeps = sleeps;
            this.result = result;
        }

        @Override
        public ListenerResult call() throws InterruptedException {
            if (sleeps) {
                TimeUnit.SECONDS.sleep(10);
            }
            return result;
        }
    }

    @Test
    void onlyOpensFiveConnectionsAtATime() throws InterruptedException {
        // Assemble
        int numConnections = 5;

        // Create a fake listener that lives longer than this test will run
        NumberListenerFactory numberListenerFactory = mock(NumberListenerFactory.class);
        when(numberListenerFactory.getInstance())
                .thenReturn(new NumberListenerExtension(true, ListenerResult.CLOSED));

        try (
                NumberServer subject = new NumberServer(numConnections, numberListenerFactory)) {
            // Act
            /*
             * Start the server in a separate thread, let it run for a second, then
             * interrupt it to kill all connections
             */
            Runnable serverRunnable = () -> subject.run();
            Thread thread = new Thread(serverRunnable);
            thread.start();
            TimeUnit.SECONDS.sleep(1);
            thread.interrupt();

            // Assert
            verify(numberListenerFactory, times(numConnections)).getInstance();
        }
    }

    @Test
    void terminatesOnClientSignal() throws InterruptedException {
        // Assemble
        int numConnections = 5;

        // One client should terminate and the rest will sleep for 10 seconds
        NumberListenerFactory numberListenerFactory = mock(NumberListenerFactory.class);
        when(numberListenerFactory.getInstance()).thenReturn(
                new NumberListenerExtension(false, ListenerResult.TERMINATED),
                new NumberListenerExtension(true, ListenerResult.CLOSED));

        try (
                NumberServer subject = new NumberServer(numConnections, numberListenerFactory)) {
            // Act
            /*
             * Start the server in a separate thread then let it run for two seconds at most
             */
            Instant startTime = Instant.now();
            Runnable serverRunnable = () -> subject.run();
            Thread thread = new Thread(serverRunnable);
            thread.start();
            thread.join(2000);
            Instant endTime = Instant.now();

            // Assert
            assertTrue(Math.abs(Duration.between(startTime, endTime).toMillis()) < 1000,
                    "Terminate command should have stopped the server before a second");
        }
    }
}
