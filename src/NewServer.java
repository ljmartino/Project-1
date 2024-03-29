import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NewServer {
    public static void main(String[] args) throws IOException {
        int portNumber = 1111;
        int numClients;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server created");
            
            // Get the filename from the user
            BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the filename: ");
            String fileName = terminalInput.readLine();

//            // Read the entire file
//            List<String> fileLines = readFileLines(fileName);

            // Get the number of expected clients
            System.out.print("Enter the number of expected clients: ");
            int expectedClients = Integer.parseInt(terminalInput.readLine());

            // Wait for the specified number of clients to connect
            System.out.println("Waiting for " + expectedClients + " clients to connect...");
            ArrayList<Socket> clientSockets = waitForClients(serverSocket, expectedClients);

//            // Wait until the "run" command is received from the terminal
//            waitForRunCommand(terminalInput);
            
            // Create a new thread to handle the clients
            Thread clientHandler = new Thread(new NewClientHandler(clientSockets, fileName));
            clientHandler.start();
//
//            // Calculate the chunk size for each client
//            int chunkSize = calculateChunkSize(fileLines.size(), expectedClients);
//
//            // Send chunks to the clients and receive word count
//            int totalWordCount = distributeChunks(clientSockets, fileLines, chunkSize);
//
//            // Print the final result
//            System.out.println("Total word count: " + totalWordCount);


           // ArrayList<Socket> list = new ArrayList<Socket>();
//            while (list.size()<numClients) {
//                Socket clientSocket = serverSocket.accept();
//                list.add(clientSocket);
//                System.out.println("Connection accepted");
//            }

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<Socket> waitForClients(ServerSocket serverSocket, int expectedClients) {
        int connectedClients = 0;
        ArrayList<Socket> clientSockets = new ArrayList<>();

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
}
