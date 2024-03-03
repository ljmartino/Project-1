import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class NewClientHandler implements Runnable {
    private static ArrayList<Socket> sockets;
    private static String filename;
    private static int numClients;
    private static long start;
    private static long end;

    public NewClientHandler(ArrayList<Socket> clientSockets, String path) {
        sockets = clientSockets;
        filename = path;
        numClients = sockets.size();
    }

    @Override
    public void run() {
        try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            waitForRunCommand(stdIn);
            stdIn.close();
        	
        	int totalWords = 0;
            List<String> fileLines = calculateFileLines(filename);

            ObjectInputStream[] inReaders = new ObjectInputStream[numClients];
            
            for(int i=0;i<sockets.size();i++){
                distributeChunks(fileLines);
                
                end = System.currentTimeMillis();
                System.out.println("Time Taken: " + (end - start) + " ms");
                
                inReaders[i] = new ObjectInputStream(sockets.get(i).getInputStream());
                System.out.println("READERS INITIALIZED");
            }
            
            System.out.println("ATTEMPTING TO READ WORDS");
            
            for(int i=0;i<sockets.size();i++){
                int words = inReaders[i].read();
                System.out.println("WORDS PARSED");
                totalWords+=words;
                System.out.println("Client " + i + ": " + words);
            }
            
            System.out.println("Total words is: " + totalWords);

        } catch (IOException e) {
            System.out.println("Exception caught when handling client");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<String> calculateFileLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    
    private static void waitForRunCommand(BufferedReader terminalInput) throws IOException {
        System.out.print("Enter 'run' to start processing: ");
        while (true) {
            String command = terminalInput.readLine();
            if ("run".equalsIgnoreCase(command)) {
                System.out.println("Received 'run' command. Starting processing...");
                start = System.currentTimeMillis();
                break;
            } else {
                System.out.println("Invalid command. Please enter 'run'.");
                System.out.print("Enter 'run' to start processing: ");
            }
        }
    }
    
    private static void distributeChunks(List<String> fileLines) throws IOException {
        int chunkSize = fileLines.size() / numClients;
    	
    	for (Socket clientSocket : sockets) {
		    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

		        // Send a chunk of lines as a file to the client
		        int startIndex = sockets.indexOf(clientSocket) * chunkSize;
		        int endIndex = Math.min((sockets.indexOf(clientSocket) + 1) * chunkSize, fileLines.size());

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
		        System.out.println("CHUNK SENT SUCCESSFULLY");
		        
		        // Signal end of chunk
		        objectOutputStream.writeObject("END_OF_CHUNK");

		    } catch (IOException e) {
		        System.out.println("Error sending/receiving chunks to/from the client");
		        e.printStackTrace();
		    }
		}
    }
}