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
            int count;
             
            // Initiate conversation with client
            // WordCount wc = new WordCount();
            // count = wc.wordCount(null);
            count = 0;
            out.println(count);
 
            while ((inputLine = in.readLine()) != null) {
                // count = wc.wordCount(inputLine);
                // count = wordCount(inputLine);
                count+=3; //Response from server just increases by 3 to show how it works
                out.println(count);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public static int wordCount(String path) throws FileNotFoundException
	{
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();
		
		Scanner reader = new Scanner(file);
		
	    int wordCount = 0;
		
	    // 1. read file line by line, count # of words, accumulate result
	    // 2. this approach is faster for large file, limits stack overflow error
		while(reader.hasNext())
			wordCount += reader.nextLine().trim().split("\\s+").length;
		
	    reader.close();
	    return wordCount;
	}
}
