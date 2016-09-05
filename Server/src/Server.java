/**
 * Created by Maverick on 5/9/16.
 */
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.*;
import java.io.*;
import java.net.*;


class Client implements Serializable {
    String uname;
    Socket clientSocket;

    public Client(String uname, Socket socket){
        this.uname=uname;
        clientSocket=socket;
    }
}
public class Server {
    ServerSocket serverSocket;
    ArrayList<Client> connectedClients;
    AcceptorThread acceptorThread;
    Thread currentThread;

    public Server(String ip, int port)
    {
        try {
            connectedClients = new ArrayList<>();
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(ip),port));
        }
        //TODO: Handle all exceptions properly.
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Socket find(String uname){
        for(Client user:connectedClients){
            if(user.uname.compareTo(uname)==0)
                return user.clientSocket;
        }
        return null;
    }


    public void startChatSystem() {
        acceptorThread=new AcceptorThread(this);
        acceptorThread.start();


    }

    public void displayActiveClients(Socket client){
        try {
            String msg="";
            if (connectedClients.size() == 1) {
                msg="Only you are active.";
            }
            else {
                msg = "Active clients: ";
                for (Client cli : connectedClients) {
                    msg += cli.uname + " ";
                }
            }
            client.getOutputStream().write(msg.getBytes());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[]args) throws IOException{
        Server server=new Server(args[0],Integer.parseInt(args[1]));
        if(server.serverSocket.isBound())
        {
            System.out.println("Server bound and waiting...");
            server.startChatSystem();
        }
        else{
            System.out.println("Server not bound yet.");
        }
    }
}
