package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerListner {
    private MultiUser mu=new MultiUser();

    public ServerListner() {
        try (ServerSocket serverSocket = new ServerSocket(55555);) {
            while (true){
                Socket socket= serverSocket.accept();
                Server ss=new Server(socket, mu);
                ss.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ServerListner s1=new ServerListner();
    }
}
