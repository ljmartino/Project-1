import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private ArrayList<Socket> sockets;

    public ClientHandler(ArrayList<Socket> sockets) {
        this.sockets = sockets;
    }

    @Override
    public void run() {
        try (
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        ) {
            int numberClients = sockets.size();
            
            System.out.println("Enter file name: ");
            String fromServer = stdIn.readLine();

            int totalWords = 0;
            long start, end;

            start = System.currentTimeMillis();
            for(int i=0;i<sockets.size();i++){
                PrintWriter out = new PrintWriter(sockets.get(i).getOutputStream(), true);
                out.println(numberClients);
                out.println(i);
                out.println(fromServer);

                BufferedReader in = new BufferedReader(new InputStreamReader(sockets.get(i).getInputStream()));

                int words = Integer.parseInt(in.readLine());
                totalWords+=words;
                System.out.println("Client "+(i+1)+": "+words);
            }
            end = System.currentTimeMillis();
            System.out.println("Total words is: "+totalWords);
            System.out.println((end-start)+" milliseconds elapsed");

        } catch (IOException e) {
            System.out.println("Exception caught when handling client");
            System.out.println(e.getMessage());
        }
    }
}