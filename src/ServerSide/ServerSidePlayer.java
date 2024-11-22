package ServerSide;

// Endast så Erik kan hämta.

import java.net.Socket;

public class ServerSidePlayer {
    private final Socket sock;
    private final String name;
    private ServerSidePlayer opponent;

    public ServerSidePlayer(Socket sock, String name) {
        this.sock = sock;
        this.name = name;
    }

    public void setOpponent(ServerSidePlayer opponent) {
        this.opponent = opponent;
    }

    public Socket getSock() {
        return sock;
    }

    public String getName() {
        return name;
    }

    public ServerSidePlayer getOpponent() {
        return opponent;
    }
}