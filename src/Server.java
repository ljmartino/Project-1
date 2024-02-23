import java.net.*;
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
 
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine;
            int count;
             
            // Initiate conversation with client
            WordCount wc = new WordCount();
            count = wc.wordCount(null);
            out.println(count);
 
            while ((inputLine = in.readLine()) != null) {
                count = wc.wordCount(inputLine);
                out.println(count);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
