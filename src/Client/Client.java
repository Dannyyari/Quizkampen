package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    InetAddress iadr= InetAddress.getLoopbackAddress();
    int port= 55555;

    private GameGUI game;
    private Socket serverSocket;
    private BufferedReader inFromServer;
    private PrintWriter outToServer;

    private int pointCounter;

    //väldigt oklart om vi ens ska ha denna konstruktor
    public Client(String serverAddress, int serverPort, GameGUI game) throws IOException {
        serverSocket =new Socket(serverAddress, serverPort);
        inFromServer =new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        outToServer=new PrintWriter(serverSocket.getOutputStream());
        this.game=game;
    }


}
