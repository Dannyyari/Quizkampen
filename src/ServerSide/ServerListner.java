package ServerSide;

import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListner {
    private int port= 55555;

    public ServerListner() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("QUIZKAMPEN");
            while (true){
                System.out.println("väntar på spelare");
                String nameP1= JOptionPane.showInputDialog("VAD HETER DU");
                ServerSidePlayer player1= new ServerSidePlayer(serverSocket.accept(), nameP1);
                System.out.println("en spelare ansluten");

                String nameP2= JOptionPane.showInputDialog("VAD HETER DU");
                ServerSidePlayer player2= new ServerSidePlayer(serverSocket.accept(), nameP2);
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
