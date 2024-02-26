import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
         
        // if (args.length != 1) {
        //     System.err.println("Input port number only");
        //     System.exit(1);
        // }
            
        //If we figure out command line, could 
        //replace 1111 with Integer.parseInt(args[0])
        int portNumber = 1111;
 
        try 
        { 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server created");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection accepted");
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
         
         
            String inputLine;
            
            out.println("words.txt");

            while ((inputLine = in.readLine()) != null) {
                out.println("words.txt");
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    
}
