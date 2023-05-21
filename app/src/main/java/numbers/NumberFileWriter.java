package numbers;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class NumberFileWriter implements Closeable {
    private BufferedWriter writer;

    /**
     * Opens the file for write and clears the contents
     * @throws IOException if the file could not be opened.
     */
    public NumberFileWriter(Path file) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(
            file,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
        this.writer = writer;
    }

    /**
     * Writes the value and a newline character
     * @param value to be written to the file
     * @throws IOException if the file could not be written
     */
    public synchronized void write(String value) throws IOException {
        writer.write(value);
        writer.newLine();
        writer.flush();
    }

    public void close() throws IOException {
        if(writer != null) {
            writer.close();
        }
    }
}
