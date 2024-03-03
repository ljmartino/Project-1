import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String hostName = "192.168.0.35"; // Server's IP
		int portNumber = 1111;

		try 
		{
			Socket mySocket = new Socket(hostName, portNumber);
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			Object chunk;
			int wordCount = 0;

			// Read data from server
			while ((chunk = objectInputStream.readObject()) != null) {
				if (chunk instanceof String && chunk.equals("END_OF_CHUNK")) {
					// End of chunk, break the loop
					break;
				}

				System.out.println("RECEIVED FILE CHUNK FROM SERVER");

				// Process the chunk
				if (chunk instanceof File) {
					File tempFile = (File) chunk;
					wordCount = wordCount(tempFile);
					System.out.println("Word count of file chunk: " + wordCount);
				}
			}

			// Send word count back to server
			PrintWriter outWriter = new PrintWriter(mySocket.getOutputStream(), true);
			outWriter.println(wordCount);
			mySocket.shutdownOutput();
			System.out.println("WORDS SENT SUCCESSFULLY");

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
