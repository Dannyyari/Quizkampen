package ServerSide;

import javax.swing.*;
import java.net.ServerSocket;

public class ServerListener {
    private int port= 55555;

    public ServerListener() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("Quizkampens server är igång...");

            while (true){

                System.out.println("Väntar på spelare...");
                String nameP1 = JOptionPane.showInputDialog("VAD HETER DU?");
                ServerSidePlayer player1 = new ServerSidePlayer(serverSocket.accept(), nameP1);
                System.out.println("En spelare ansluten!");

                String nameP2 = JOptionPane.showInputDialog("VAD HETER DU?");
                ServerSidePlayer player2 = new ServerSidePlayer(serverSocket.accept(), nameP2);
                System.out.println("Två spelare anslutna, nu kör vi!");

                Server server = new Server(player1, player2);
                server.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ServerListener s1 = new ServerListener();
    }
}
