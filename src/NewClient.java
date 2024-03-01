import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class NewClient {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String hostName = "192.168.0.34"; // Replace with the server's IP address or hostname
		int portNumber = 1111;

		try 
		{
			Socket socket = new Socket(hostName, portNumber);
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			BufferedReader stdIn =
					new BufferedReader(new InputStreamReader(System.in));


			// Receive file chunks and process them
			Object chunk;
			int wordCount = 0;

			while ((chunk = objectInputStream.readObject()) != null) {
				if ("END_OF_CHUNK".equals(chunk)) {
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

				//            String fromServer;
				//            int count;

				//            fromServer = in.readLine();
				//            count = wordCount(fromServer);
				System.out.println(wordCount);
				out.println(wordCount);
			}

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection");
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
