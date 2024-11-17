package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerListner {

    public ServerListner() {
        try (ServerSocket serverSocket = new ServerSocket(55555);) {
            while (true){
                Socket socketForPlayer1= serverSocket.accept();
                System.out.println("en spelare ansluten");
                Socket socketForPlayer2 = serverSocket.accept();
                System.out.println("två spelare anslutna, nu kör vi!");
                Server gameServer= new Server(socketForPlayer1, socketForPlayer2);
                gameServer.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ServerListner s1=new ServerListner();
    }
}
