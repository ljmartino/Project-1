import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
         
        // if (args.length != 2) {
        //     System.err.println("Input host name and port number only");
        //     System.exit(1);
        // }
        
        //If we figure out command line, could 
        //replace server.example.com with args[0] 
        // and 1111 with Integer.parseInt(args[0])
        String hostName = "10.111.142.92"; //This is my laptop's IP address
                                           //you can replace it with yours 
                                           //to test just change it back before pushing
        int portNumber = 1111;
 
        try 
        {
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            
            //Server has to communicate first or else loop won't run
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                 
                fromUser = stdIn.readLine();
                if(fromUser.equals("end")) break;
                else if (fromUser.equals("run")) {
                    int response = wordCount(fromServer);
                    System.out.println("Client: " + response);
                    out.println(response);
                } else{
                    System.out.println("Invalid input, try again");
                    out.println("Invalid input, try again");
                }
                System.out.println();
            }



        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
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
