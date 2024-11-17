package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    GameGUI game=new GameGUI();

    private Socket serverSocket;
    private BufferedReader inFromServer;
    private PrintWriter outToServer;

    public Client(String serverAddress, int serverPort) throws IOException {
        serverSocket =new Socket(serverAddress, serverPort);
        inFromServer =new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        outToServer=new PrintWriter(serverSocket.getOutputStream());
    }


}
