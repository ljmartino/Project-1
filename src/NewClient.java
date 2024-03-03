import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class NewClient {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String hostName = "192.168.0.35"; // Replace with the server's IP address or hostname
		int portNumber = 1111;

		try 
		{
			Socket mySocket = new Socket(hostName, portNumber);
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());

			// Receive file chunks and process them
			Object chunk;
			int wordCount = 0;

			while ((chunk = objectInputStream.readObject()) != null) {
				if (chunk instanceof String && chunk.equals("END_OF_CHUNK")) {
					System.out.println("END OF CHUNK DETECTED");
					// End of chunks, break the loop
					break;
				}

				System.out.println("Received file chunk from server.");

				// Process the chunk
				if (chunk instanceof File) {
					File tempFile = (File) chunk;
					wordCount = wordCount(tempFile);
					System.out.println("Word count of file chunk: " + wordCount);
				}
			}

			System.out.println("LOOP BROKEN SUCCESSFULLY");
			
			if(mySocket.isClosed())
				System.out.println("At client: Socket is closed");
			else {
				System.out.println("At client: Socket is open");
				PrintWriter outWriter = new PrintWriter(mySocket.getOutputStream());
				outWriter.println(wordCount);
				System.out.println("WORDS SENT");
			}

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static int wordCount(File file) throws FileNotFoundException
	{	
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
