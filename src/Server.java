import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 1111;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server created");

            // Get the filename from the user
            BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the filename: ");
            String fileName = terminalInput.readLine();

            // Read the entire file
            List<String> fileLines = readFileLines(fileName);

            // Get the number of expected clients
            System.out.print("Enter the number of expected clients: ");
            int expectedClients = Integer.parseInt(terminalInput.readLine());

            // Wait for the specified number of clients to connect
            System.out.println("Waiting for " + expectedClients + " clients to connect...");
            List<Socket> clientSockets = waitForClients(serverSocket, expectedClients);

            // Wait until the "run" command is received from the terminal
            waitForRunCommand(terminalInput);

            // Calculate the chunk size for each client
            int chunkSize = calculateChunkSize(fileLines.size(), expectedClients);

            // Send chunks to the clients and receive word count
            int totalWordCount = distributeChunks(clientSockets, fileLines, chunkSize);

            // Print the final result
            System.out.println("Total word count: " + totalWordCount);

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static List<String> readFileLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<Socket> waitForClients(ServerSocket serverSocket, int expectedClients) {
        int connectedClients = 0;
        List<Socket> clientSockets = new ArrayList<>();

        while (connectedClients < expectedClients) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from Client " + (connectedClients + 1));
                clientSockets.add(clientSocket);
                connectedClients++;
            } catch (IOException e) {
                break;
            }
        }

        System.out.println("All expected clients connected.");
        return clientSockets;
    }

    private static void waitForRunCommand(BufferedReader terminalInput) throws IOException {
        System.out.print("Enter 'run' to start processing: ");
        while (true) {
            String command = terminalInput.readLine();
            if ("run".equalsIgnoreCase(command)) {
                System.out.println("Received 'run' command. Starting processing...");
                break;
            } else {
                System.out.println("Invalid command. Please enter 'run'.");
                System.out.print("Enter 'run' to start processing: ");
            }
        }
    }

    private static int calculateChunkSize(int totalLines, int totalClients) {
        // Calculate the chunk size for each client
        return totalLines / totalClients;
    }

    private static int distributeChunks(List<Socket> clientSockets, List<String> fileLines, int chunkSize) {
        int totalWordCount = 0;

        try {
            for (Socket clientSocket : clientSockets) {
                try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    // Send a chunk of lines as a file to the client
                    int startIndex = clientSockets.indexOf(clientSocket) * chunkSize;
                    int endIndex = Math.min((clientSockets.indexOf(clientSocket) + 1) * chunkSize, fileLines.size());

                    // Create a temporary file for the chunk
                    Path tempFile = Files.createTempFile("chunk", ".txt");
                    try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                        for (int i = startIndex; i < endIndex; i++) {
                            writer.write(fileLines.get(i));
                            writer.newLine();
                        }
                    }

                    // Send the file to the client
                    objectOutputStream.writeObject(tempFile.toFile());

                    // Signal the end of the chunks
                    out.println("END_OF_CHUNK");

                    // Receive word count from the client (remove this line)
                    // String wordCountStr = in.readLine();

                    // The rest of the code remains the same...

                } catch (IOException e) {
                    System.out.println("Error sending/receiving chunks to/from the client");
                    e.printStackTrace();
                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when accepting client connections");
            e.printStackTrace();
        }

        return totalWordCount;
    }
}