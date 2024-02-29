import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 1111;

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server created");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection accepted");

                // Create a new thread to handle the client
                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
