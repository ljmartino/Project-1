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

	public static int wordCount2(String path, int start, int end) throws IOException {
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();
		
		Scanner reader = new Scanner(file);

	    int wordCount = 0;
    	int noOfLines = start;
		boolean newLine = true;

    	try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
    	    ByteBuffer byteBuffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
     	   	while(byteBuffer.hasRemaining() && reader.hasNext()) {
				if(noOfLines>=start && noOfLines<=end && newLine){
					wordCount += reader.nextLine().trim().split("\\s+").length;
					noOfLines++;
				}
				else{
     	       		byte currentByte = byteBuffer.get();
    	    		if (currentByte == '\n'){
     	           		noOfLines++;
						newLine = true;
					} else{
						newLine = false;
					}
				}
     	  	}
    	}
		return wordCount;
	}

	public static long numBits(String path) throws IOException {
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();

		return file.length();
	}

	public static int wordCount3(String path, long start, long end) throws IOException {
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();
		
		Scanner reader = new Scanner(file);

	    int wordCount = 0;
    	long noOfBytes = (byte)start;
		boolean newLine = true;

    	try (FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
    	    ByteBuffer byteBuffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
     	   	while(byteBuffer.hasRemaining() && reader.hasNext()) {
				if(noOfBytes>=start && noOfBytes<=end && newLine){
					int word = reader.nextLine().trim().split("\\s+").length;
					wordCount += word;
					noOfBytes+=(byte)word;
				}
				else{
     	       		byte currentByte = byteBuffer.get();
    	    		if (currentByte == '\n'){
     	           		noOfBytes++;
						newLine = true;
					} else{
						newLine = false;
					}
				}
     	  	}
    	}
		return (int)noOfBytes;
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
			int numLines = numberLines("words.txt");
			int beginning = wordCount("words.txt", 0, numLines/2);
			int end = wordCount("words.txt", numLines/2+1, numLines);
			System.out.println(beginning);
			System.out.println(end);
			System.out.println(beginning+end);
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
