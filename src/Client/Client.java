package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread implements Serializable {
    InetAddress iadr = InetAddress.getLoopbackAddress();
    int port = 55555;


    //  private Socket clientSocket;
    // ObjectOutputStream outToServer;
    // ObjectInputStream inFromServer;

    GameGUI game;

    private int pointCounter;


    //väldigt oklart om vi ens ska ha denna konstruktor
    public Client() throws IOException {
        try (Socket clientSocket=new Socket(iadr,port);
             ObjectOutputStream outToServer=new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
          ){

            System.out.println(inFromServer.readObject());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client=new Client();

    }

    public void startGame() throws IOException {
    }

//    public void sendAnswer(int answerIndex) throws IOException {
//        outToServer.writeObject(answerIndex);
//    }
//
//    public Object receiveFromServer() throws IOException, ClassNotFoundException {
//        return inFromServer.readObject();
//    }
}
