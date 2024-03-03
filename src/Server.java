import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
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

            // Get the number of expected clients
            System.out.print("Enter the number of expected clients: ");
            int expectedClients = Integer.parseInt(terminalInput.readLine());

            // Wait for the specified number of clients to connect
            System.out.println("Waiting for " + expectedClients + " clients to connect...");
            ArrayList<Socket> clientSockets = waitForClients(serverSocket, expectedClients);
            
            // Waits for "run" from terminal to start word count, captures start time
            long start = waitForRunCommand(terminalInput);
            
            // Total words in file
            int totalWords = 0;
            
            // Get file as array of file lines
            List<String> fileLines = calculateFileLines(fileName);
            
        	// Declare and initialize arrays for socket I/O stream
            ObjectOutputStream[] outWriters = new ObjectOutputStream[expectedClients];
        	BufferedReader[] inReaders = new BufferedReader[expectedClients];
        	
        	for(int i = 0; i < clientSockets.size(); i++)
        	{
        		outWriters[i] = new ObjectOutputStream(clientSockets.get(i).getOutputStream());
				inReaders[i] = new BufferedReader(new InputStreamReader(clientSockets.get(i).getInputStream()));
        	}

            // Divide file and send chunks to each client
        	distributeChunks(clientSockets, fileLines, outWriters);
            
        	// Read word counts from each client and print result
            for(int i=0;i<clientSockets.size();i++){
                int words = Integer.parseInt(inReaders[i].readLine());
                totalWords+=words;
                System.out.println("Client " + (i + 1) + ": " + words);       
            }
            
            // Display final word count of file
            System.out.println("Total words in file: " + totalWords);
            
            // Capture end time and print time elapsed for operation
            long end = System.currentTimeMillis();
            System.out.println("Time Taken: " + (end - start) + " ms");

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    // Ensure the server won't begin running until the expected number of clients connect
    private static ArrayList<Socket> waitForClients(ServerSocket serverSocket, int expectedClients) {
        int connectedClients = 0;
        ArrayList<Socket> clientSockets = new ArrayList<>();

        // Accept client connections until expected number is reached
        while (connectedClients < expectedClients) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted from Client " + (connectedClients + 1));
                
                // Add client socket to list
                clientSockets.add(clientSocket);
                connectedClients++;
            } catch (IOException e) {
                break;
            }
        }

        System.out.println("All expected clients connected.");
        
        // Return list of client sockets for clients successfully connected
        return clientSockets;
    }
    
    // Read file and save contents as list of strings
    private static List<String> calculateFileLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        }
        return lines;
    }
    
    // Wait for run command before server begins distributing file chunks to clients
    private static long waitForRunCommand(BufferedReader terminalInput) throws IOException {
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
        
        // Return start time before server begins operation
        return System.currentTimeMillis();
    }
    
    // Divide file and distribute chunks to each client
    private static void distributeChunks(ArrayList<Socket> sockets, List<String> fileLines, ObjectOutputStream[] outWriters) throws IOException {
        // Calculate size for each chunk (rounded up so no lines are missed)
    	int chunkSize = (int) Math.round(Math.ceil(fileLines.size() / (double) sockets.size()));
        
    	// Create chunks and distribute them to appropriate clients
    	for (Socket clientSocket : sockets) {
		    try {
		        // Determine number of lines to include in file chunk
		        int socketIndex = sockets.indexOf(clientSocket);
		    	int startIndex = socketIndex * chunkSize;
		        int endIndex = Math.min((socketIndex + 1) * chunkSize, fileLines.size());
		        
		        // Create a temporary file for the chunk
		        Path tempFile = Files.createTempFile("chunk", ".txt");
		        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
		            for (int i = startIndex; i < endIndex; i++) {
		                writer.write(fileLines.get(i));
		                writer.newLine();
		            }
		        }

		        // Send the file chunk to the client
		        outWriters[socketIndex].writeObject(tempFile.toFile());
		        
		        // Signal end of chunk
		        outWriters[socketIndex].writeObject("END_OF_CHUNK");

		    } catch (IOException e) {
		        System.out.println("Error sending/receiving chunks to/from the client");
		        e.printStackTrace();
		    }
		}
    	
    	System.out.println("SENT CHUNKS TO CLIENTS");
    }
}
