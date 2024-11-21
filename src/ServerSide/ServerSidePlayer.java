package ServerSide;

import java.net.Socket;

public class ServerSidePlayer {
    private Socket sock;
    private String name;
    private ServerSidePlayer opponent;

    public ServerSidePlayer(Socket sock, String name) {
        this.sock = sock;
        this.name = name;
    }

    public void setOpponent(ServerSidePlayer opponent){
        this.opponent=opponent;
    }

    //för att flippa i handleround
    public String getName() {
        return name;
    }

    public Socket getSock() {
        return sock;
    }

    public ServerSidePlayer getOpponent() {
        return opponent;
    }
}
