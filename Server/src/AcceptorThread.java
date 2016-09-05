import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by saarang on 5/9/16.
 */
public class AcceptorThread extends Thread{
    Server server;
    public AcceptorThread(Server server){
        this.server=server;
    }

    public void run(){
        while (true) {
            try{
                Socket client = server.serverSocket.accept();
                if (client != null) {
                    server.currentThread.sleep(1000);
                      /*
                            Maverick: Receiving the username.
                      */
                    byte[] uname = new byte[100];
                    int nameLen=client.getInputStream().read(uname);
                    Client temp = new Client((new String(uname,0,nameLen)), client);
                    server.connectedClients.add(temp);
                    System.out.println("New client: " + new String(uname));

                    /*
                        Maverick: Display all the active clients to the new client.
                     */
                    server.displayActiveClients(client);

                    /*
                        Maverick: Deploy a new thread for the new client.
                     */
                    new ChatThread(server,client,new String(uname,0,nameLen)).start();
                }

            }
            catch(Exception e)
            {

            }
        }
    }
}
