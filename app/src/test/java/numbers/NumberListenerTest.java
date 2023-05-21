package numbers;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NumberListenerTest {
    @Test
    void writesUniqueNumbersAndSkipsDuplicateNumbers() throws Exception {
        // Assemble
        String[] inputs = new String[] {
                "000000000",
                "000000001",
                "000000002",
                "000000003",
                "000000001",
                "000000002",
                "000000003",
                "000000004",
                "000000005",
                "000000006",
        };
        String input = String.join(System.getProperty("line.separator"), inputs);
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Socket socket = mock(Socket.class);
        ServerSocket server = mock(ServerSocket.class);
        when(server.accept()).thenReturn(socket);
        when(socket.getInputStream()).thenReturn(inputStream);

        NumberTracker numberTracker = new NumberTracker();
        NumberFileWriter fileWriter = mock(NumberFileWriter.class);

        NumberListener subject = new NumberListener(server, numberTracker, fileWriter);

        // Act
        ListenerResult result = subject.call();

        // Assert
        assertEquals(ListenerResult.CLOSED, result, "Client should have closed the connection");

        ArgumentCaptor<String> numberCaptor = ArgumentCaptor.forClass(String.class);
        Set<String> expectedNumbers = Stream.of(inputs).collect(Collectors.toSet());
        verify(fileWriter, times(expectedNumbers.size())).write(numberCaptor.capture());
        assertEquals(expectedNumbers, numberCaptor.getAllValues().stream().collect(Collectors.toSet()));
    }

    @Test
    void closesConnectionOnInvalidInput() throws Exception {
        // Assemble
        String[] inputs = new String[] {
                "000000001",
                "000000002",
                "000000003",
                "invalid",
                "000000004",
                "000000005",
                "000000006",
        };
        String input = String.join(System.getProperty("line.separator"), inputs);
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Socket socket = mock(Socket.class);
        ServerSocket server = mock(ServerSocket.class);
        when(server.accept()).thenReturn(socket);
        when(socket.getInputStream()).thenReturn(inputStream);

        NumberTracker numberTracker = new NumberTracker();
        NumberFileWriter fileWriter = mock(NumberFileWriter.class);

        NumberListener subject = new NumberListener(server, numberTracker, fileWriter);

        // Act
        ListenerResult result = subject.call();

        // Assert
        assertEquals(ListenerResult.CLOSED, result, "Client should have closed the connection");

        ArgumentCaptor<String> numberCaptor = ArgumentCaptor.forClass(String.class);
        Set<String> expectedNumbers = Stream.of("000000001", "000000002", "000000003").collect(Collectors.toSet());
        verify(fileWriter, times(expectedNumbers.size())).write(numberCaptor.capture());
        assertEquals(expectedNumbers, numberCaptor.getAllValues().stream().collect(Collectors.toSet()),
                "Should only have tracked the numbers before the invalid input");
    }

    @Test
    void returnsTerminatedOnTerminateMessage() throws Exception {
        // Assemble
        String[] inputs = new String[] {
                "000000001",
                "000000002",
                "000000003",
                "terminate",
                "000000004",
                "000000005",
                "000000006",
        };
        String input = String.join(System.getProperty("line.separator"), inputs);
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Socket socket = mock(Socket.class);
        ServerSocket server = mock(ServerSocket.class);
        when(server.accept()).thenReturn(socket);
        when(socket.getInputStream()).thenReturn(inputStream);

        NumberTracker numberTracker = new NumberTracker();
        NumberFileWriter fileWriter = mock(NumberFileWriter.class);

        NumberListener subject = new NumberListener(server, numberTracker, fileWriter);

        // Act
        ListenerResult result = subject.call();

        // Assert
        assertEquals(ListenerResult.TERMINATED, result, "Client should have terminated the connection");

        ArgumentCaptor<String> numberCaptor = ArgumentCaptor.forClass(String.class);
        Set<String> expectedNumbers = Stream.of("000000001", "000000002", "000000003").collect(Collectors.toSet());
        verify(fileWriter, times(expectedNumbers.size())).write(numberCaptor.capture());
        assertEquals(expectedNumbers, numberCaptor.getAllValues().stream().collect(Collectors.toSet()),
                "Should only have tracked the numbers before the terminate command");
    }
}
