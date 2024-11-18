package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    InetAddress iadr = InetAddress.getLoopbackAddress();
    int port = 55555;

    private Socket serverSocket;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;
    private GameGUI game;

    // FRÅGA! Vi skulle väl ha OOS och OIS istället för BR och PW?
    // private BufferedReader inFromServer;
    // private PrintWriter outToServer;

    private int pointCounter;

    //väldigt oklart om vi ens ska ha denna konstruktor
    public Client(String serverAddress, int serverPort, GameGUI game) throws IOException {
        serverSocket = new Socket(serverAddress, serverPort);
        outToServer = new ObjectOutputStream(serverSocket.getOutputStream());
        inFromServer = new ObjectInputStream(serverSocket.getInputStream());
        this.game = game;

        // FRÅGA! Vi skulle väl ha OOS och OIS istället för BR och PW?
        // inFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        // outToServer = new PrintWriter(serverSocket.getOutputStream());

    }

    public void startGame() throws IOException {
    }

    public void sendAnswer(int answerIndex) throws IOException {
        outToServer.writeObject(answerIndex);
    }

    public Object receiveFromServer() throws IOException, ClassNotFoundException {
        return inFromServer.readObject();
    }
}
