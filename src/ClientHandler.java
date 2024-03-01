import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final List<String> fileLines;
    private final int startIndex;
    private final int endIndex;

    public ClientHandler(Socket clientSocket, List<String> fileLines, int startIndex, int endIndex) {
        this.clientSocket = clientSocket;
        this.fileLines = fileLines;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            System.out.println("Connection accepted from Client");

            // Receive the current chunk from the client
            StringBuilder chunkBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !"END_OF_CHUNK".equals(line)) {
                chunkBuilder.append(line).append("\n");
            }

            // Process the chunk and count words
            String chunk = chunkBuilder.toString();
            int wordCount = countWords(chunk);

            // Send the word count back to the server
            out.println(wordCount);

            // Close the connection
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int countWords(String text) {
        String[] words = text.split("\\s+");
        return words.length;
    }
}
