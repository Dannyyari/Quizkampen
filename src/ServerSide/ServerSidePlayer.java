package ServerSide;

import java.net.Socket;

/*
 Klassen ServerSidePlayer representerar en spelare på serversidan av ett nätverksspel.
 Varje spelare har en socket för nätverkskommunikation, ett namn för identifiering
 och en referens till en motståndare. Klassen innehåller metoder för att:
 - Ställa in och hämta spelarens namn.
 - Ställa in och hämta spelarens socket (anslutning).
 - Länka till en motståndare genom att använda en annan instans av ServerSidePlayer.

 Denna klass är en grundläggande byggsten för att hantera spelare och deras
  nätverksanslutningar i ett multiplayer-spel.
 */

public class ServerSidePlayer {
    private Socket sock; // Spelarens nätverksanslutning.
    private String name; // Spelarens namn.
    private ServerSidePlayer opponent; // Referens till spelarens motståndare.

    // Konstruktor för att initiera en spelare med socket och namn.
    public ServerSidePlayer(Socket sock, String name) {
        this.sock = sock;
        this.name = name;
    }

    // Sätt en motståndare för den här spelaren.
    public void setOpponent(ServerSidePlayer opponent) {
        this.opponent = opponent;
    }

    // Hämta spelarens namn.
    public String getName() {
        return name;
    }

    // Hämta spelarens socket.
    public Socket getSock() {
        return sock;
    }

    // Hämta spelarens motståndare.
    public ServerSidePlayer getOpponent() {
        return opponent;
    }
}