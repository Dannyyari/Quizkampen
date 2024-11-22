package ServerSide;

import javax.swing.*;
import java.net.ServerSocket;

public class ServerListener {
    private final int port = 55553;

    public ServerListener() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Quizkampens server är igång...");

            while (true) {
                System.out.println("Väntar på spelare...");
                ServerSidePlayer player1 = new ServerSidePlayer(serverSocket.accept(), "Spelare 1");
                System.out.println(player1.getName() + " ansluten!");

                ServerSidePlayer player2 = new ServerSidePlayer(serverSocket.accept(), "Spelare 2");
                System.out.println(player2.getName() + " ansluten!");

                Server server = new Server(player1, player2);
                server.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerListener();
    }
}