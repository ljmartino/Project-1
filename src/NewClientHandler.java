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
            
            System.out.println("SOCKET STATUS AFTER FILE LINES");
        	for(int i = 0; i < sockets.size(); i++)
        	{
        		String isOpen = "";
        		
        		if(sockets.get(i).isClosed())
        			isOpen = "CLOSED";
        		else {
					isOpen = "OPEN";
				}
        		System.out.println("SOCKET " + i + ": " + isOpen);
        	}

            
        	ObjectOutputStream[] outWriters = new ObjectOutputStream[numClients];
        	BufferedReader[] inReaders = new BufferedReader[numClients];
        	
        	for(int i = 0; i < sockets.size(); i++)
        	{
        		outWriters[i] = new ObjectOutputStream(sockets.get(i).getOutputStream());
				inReaders[i] = new BufferedReader(new InputStreamReader(sockets.get(i).getInputStream()));
                System.out.println("READERS INITIALIZED");
        	}
        	
        	System.out.println("SOCKET STATUS AFTER R/W INITIALIZATION");
        	for(int i = 0; i < sockets.size(); i++)
        	{
        		String isOpen = "";
        		
        		if(sockets.get(i).isClosed())
        			isOpen = "CLOSED";
        		else {
					isOpen = "OPEN";
				}
        		System.out.println("SOCKET " + i + ": " + isOpen);
        	}

            
            for(int i=0;i<sockets.size();i++){
                distributeChunks(fileLines, outWriters);
                
                System.out.println("SOCKET STATUS AFTER CHUNKS");
            	for(int ii = 0; ii < sockets.size(); ii++)
            	{
            		String isOpen = "";
            		
            		if(sockets.get(ii).isClosed())
            			isOpen = "CLOSED";
            		else {
    					isOpen = "OPEN";
    				}
            		System.out.println("SOCKET " + ii + ": " + isOpen);
            	}
                
                end = System.currentTimeMillis();
                System.out.println("Time Taken: " + (end - start) + " ms");
                
                if(sockets.get(i).isClosed())
    				System.out.println("At Server: Socket is closed");
                else {
    				System.out.println("At Server: Socket is open");
				}            
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
            reader.close();
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
    
    private static void distributeChunks(List<String> fileLines, ObjectOutputStream[] outWriters) throws IOException {
        int chunkSize = fileLines.size() / numClients;
    	
    	for (Socket clientSocket : sockets) {
		    try /*(ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream()))*/ {

		        // Send a chunk of lines as a file to the client
		        int startIndex = sockets.indexOf(clientSocket) * chunkSize;
		        int endIndex = Math.min((sockets.indexOf(clientSocket) + 1) * chunkSize, fileLines.size());
		        int writerIndex = sockets.indexOf(clientSocket);
		        
		        // Create a temporary file for the chunk
		        Path tempFile = Files.createTempFile("chunk", ".txt");
		        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
		            for (int i = startIndex; i < endIndex; i++) {
		                writer.write(fileLines.get(i));
		                writer.newLine();
		            }
		        }

		        // Send the file to the client
		        outWriters[writerIndex].writeObject(tempFile.toFile());
		        System.out.println("CHUNK SENT SUCCESSFULLY");
		        
		        // Signal end of chunk
		        outWriters[writerIndex].writeObject("END_OF_CHUNK");
		        System.out.println("END OF CHUNK SIGNALED");

		    } catch (IOException e) {
		        System.out.println("Error sending/receiving chunks to/from the client");
		        e.printStackTrace();
		    }
		}
    	
    	System.out.println("FINISHED SENDING CHUNKS");
    }
}