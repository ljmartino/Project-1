import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost"; // Replace with the server's IP address or hostname
        int portNumber = 1111;

        try (Socket socket = new Socket(hostName, portNumber);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Signal the server that the client is ready
            out.println("run");

            // Receive file chunks and process them
            String chunk;
            int wordCount = 0;
            while ((chunk = in.readLine()) != null) {
                if ("END_OF_CHUNK".equals(chunk)) {
                    // End of chunks, break the loop
                    break;
                }

                System.out.println("Received file chunk from server: " + chunk);

                // Process the chunk (you can implement your logic here)
                if (!chunk.isEmpty()) {
                    wordCount += countWords(chunk);
                }
            }

            // Send word count back to the server
            System.out.println("Sending word count to the server: " + wordCount);
            out.println(wordCount);

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }

    private static int countWords(String text) {
        String[] words = text.split("\\s+");
        int count = words.length;
        System.out.println("Word count for the current chunk: " + count);
        return count;
    }
}
