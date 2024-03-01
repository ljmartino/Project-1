import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class WordCount
{
	/*
	 * @param path path to the file
	 * @return total number of words in the file references by path
	 * @throws FileNotFoundException if file doesn't exist
	 * @precondition file must be a valid text file
	 * @postcondition wordCount represents total number of words separated by space
	 */

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

	public static void main(String[] args) throws IOException
	{
		try
		{
			long start = System.currentTimeMillis();
			int numLines = numberLines("words.txt");
			int beginning = wordCount("words.txt", 1, numLines/3);
			int middle = wordCount("words.txt", numLines/3+1, 2*numLines/3);
			int end = wordCount("words.txt", 2*numLines/3+1, numLines);
			long ending = System.currentTimeMillis();
			System.out.println(beginning);
			System.out.println(middle);
			System.out.println(end);
			System.out.println(beginning+middle+end);
			System.out.print((ending-start)+" milliseconds elapsed");
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
