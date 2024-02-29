import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;

            out.println("words.txt");

            while ((inputLine = in.readLine()) != null) {
                out.println("words.txt");
            }
        } catch (IOException e) {
            System.out.println("Exception caught when handling client");
            System.out.println(e.getMessage());
        }
    }
}