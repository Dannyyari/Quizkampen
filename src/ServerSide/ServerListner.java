package ServerSide;

import java.net.ServerSocket;

/*
 Klassen ServerListner ansvarar för att starta en server för ett nätverksspel.
 Den lyssnar på en specifik port efter inkommande anslutningar från spelarklienter.
 Funktioner:
 - Startar en server socket på en given port (55553 i detta fall).
 - Väntar på att två spelare ansluter.
 - Skapar spelare (ServerSidePlayer) för varje anslutning.
 - Startar ett nytt spel (ServerGame) när två spelare är anslutna.
 - Hanterar undantag som kan uppstå vid nätverkskommunikation.

 Klassen har också en main-metod som instansierar och kör servern.
 */

public class ServerListner {
    private int port = 55553; // Porten servern lyssnar på för anslutningar.

    // Konstruktor som startar servern och hanterar spelarsessioner.
    public ServerListner() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("QUIZKAMPEN"); // Meddelande när servern startar.

            // Oändlig loop för att hantera flera spelomgångar.
            while (true) {
                System.out.println("väntar på spelare");

                // Accepterar första spelaren.
                ServerSidePlayer player1 = new ServerSidePlayer(serverSocket.accept(), "Spelare 1");
                System.out.println("en spelare ansluten");

                // Accepterar andra spelaren.
                ServerSidePlayer player2 = new ServerSidePlayer(serverSocket.accept(), "Spelare 2");
                System.out.println("två spelare anslutna, nu kör vi!");

                // Skapar och startar ett nytt spel med de två spelarna.
                ServerGame server = new ServerGame(player1, player2);
                server.start(); // Startar spelets logik i en separat tråd.
            }
        } catch (Exception e) {
            // Hanterar fel som kan uppstå under serverns körning.
            throw new RuntimeException(e);
        }
    }

    // Main-metod för att starta servern.
    public static void main(String[] args) {
        ServerListner s1 = new ServerListner(); // Instansierar serverlyssnaren.
    }
}