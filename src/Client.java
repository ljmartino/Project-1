import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String hostName = "localhost"; // Replace with the server's IP address or hostname
        int portNumber = 1111;

        try (Socket socket = new Socket(hostName, portNumber);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            // Send the initial command to the server
            //out.println("run");

            // Receive file chunks and process them
            Object chunk;
            while ((chunk = objectInputStream.readObject()) != null) {
                if ("END_OF_CHUNK".equals(chunk)) {
                    // End of chunks, break the loop
                    break;
                }

                System.out.println("Received file chunk from server.");

                // Process the chunk (you can implement your logic here)
                if (chunk instanceof File) {
                    File tempFile = (File) chunk;
                    List<String> lines = Files.readAllLines(tempFile.toPath());
                    int wordCount = calculateWordCount(lines);
                    System.out.println("Received word count from server: " + wordCount);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    private static int calculateWordCount(List<String> lines) {
        // Implement your word count logic based on the lines received
        int wordCount = 0;
        for (String line : lines) {
            // Implement your word count logic for each line
            // For example, you can use StringTokenizer or split by space
            // and count the number of resulting tokens or words.
            // Here, a simple example is used for demonstration purposes.
            wordCount += line.split("\\s+").length;
        }
        return wordCount;
    }
}
