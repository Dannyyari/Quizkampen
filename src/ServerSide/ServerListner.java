package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerListner {
    private int port= 55555;

    public ServerListner() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("QUIZKAMPEN");
            System.out.println();
            while (true){
                ServerSidePlayer player1= new ServerSidePlayer(serverSocket.accept(), "Spelare 1");
                System.out.println("en spelare ansluten");

                ServerSidePlayer player2= new ServerSidePlayer(serverSocket.accept(), "Spelare 2");
                System.out.println("två spelare anslutna, nu kör vi!");
                Server server= new Server(player1,player2);

                server.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ServerListner s1=new ServerListner();
    }
}
