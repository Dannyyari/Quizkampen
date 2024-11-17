package Server;

import java.io.*;
import java.net.Socket;

public class ServerSide extends Thread {
    Socket socket;
    MultiUser multiUser;


    public ServerSide(Socket socket, MultiUser multiUser){
        this.socket=socket;
        this.multiUser=multiUser;
    }


    public void run(){
        try(PrintWriter toUser= new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromUser=new BufferedReader(new InputStreamReader(socket.getInputStream()));
       ){
            multiUser.addToWriter(toUser);
            String input="";

            while((input=fromUser.readLine())!=null){
                multiUser.printWriter(input);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



}
