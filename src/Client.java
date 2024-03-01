import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
         
       
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
            int numClients, numberClient, numLines, start, end, count=0;
            
            numClients = Integer.parseInt(in.readLine());
            numberClient = Integer.parseInt(in.readLine());
            System.out.println("Client number "+(numberClient+1));

            fromServer = in.readLine();

            numLines = numberLines(fromServer);
            if(numberClient==0){
                start = 1;
                end = ((numberClient+1)*numLines)/numClients;
                count = wordCount(fromServer, start, end);
            }
            if(numberClient==(numClients-1)){
                start = (numberClient*numLines)/numClients+1;
                end = numLines;
                count = wordCount(fromServer, start, end);
            }
            if(numberClient!=0 && numberClient!=(numClients-1)) {
                start = (numberClient*numLines)/numClients+1;
                end = ((numberClient+1)*numLines)/numClients;
                count = wordCount(fromServer, start, end);
            }
            System.out.println(count);
            out.println(count);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }

    public static int wordCount(String path, int start, int end) throws FileNotFoundException
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
		for(int i=1;i<=end;i++){
			if(i>=start) wordCount += reader.nextLine().trim().split("\\s+").length;
			else reader.nextLine().trim().split("\\s+");
		}

	    reader.close();
	    return wordCount;
	}

    public static int numberLines(String path) throws IOException {
    	int noOfLines = 1;

		boolean newLine = true;
		int count = 1;

    	try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
    	    ByteBuffer byteBuffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
     	   	while (byteBuffer.hasRemaining()) {
     	       	byte currentByte = byteBuffer.get();
    	    	if (currentByte == '\n'){
     	           	noOfLines++;
				}
	   	  	}
    	}
		return noOfLines;
	}
}
