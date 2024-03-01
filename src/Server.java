import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 1111;
        int numClients;

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server created");
            
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("How many clients?");
            numClients = Integer.parseInt(stdIn.readLine());

            ArrayList<Socket> list = new ArrayList<Socket>();
            while (list.size()<numClients) {
                Socket clientSocket = serverSocket.accept();
                list.add(clientSocket);
                System.out.println("Connection accepted");
            }

            // Create a new thread to handle the clients
            Thread clientHandler = new Thread(new ClientHandler(list));
            clientHandler.start();


        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
