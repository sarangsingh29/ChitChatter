/**
 * Created by Maverick on 5/9/16.
 */
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    Socket clientSocket;
    String uname;

    public Client(String uname){
        this.uname=uname;
        clientSocket=new Socket();
    }

    public boolean connectToServer(String ip, String port){
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getByName(ip), Integer.parseInt(port));
            clientSocket.connect(serverAddress);
            /*
                Maverick: Sending the username to the server.
             */
            clientSocket.getOutputStream().write(uname.getBytes());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            return clientSocket.isConnected();
        }
    }

    public void startChat(){
        try {
            InputStream consoleIn = System.in;
            InputStream socketIn = clientSocket.getInputStream();
            OutputStream socketOut = clientSocket.getOutputStream();

            byte [] msg=new byte[1000];
            int msgLen=0;

            while(true){
                msgLen=0;
                if(consoleIn.available()!=0){
                    msgLen=consoleIn.read(msg);

                    /*
                        Maverick: Sending one byte less to avoid the newline character.
                     */
                    socketOut.write(msg,0,msgLen-1);

                    if(new String(msg,0,msgLen-1).compareTo("stop")==0){
                        System.out.println("Going offline.");
                        break;
                    }
                }
                if(socketIn.available()!=0){
                    msgLen=socketIn.read(msg);
                    System.out.println(new String(msg,0,msgLen));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length!=2) return;
        System.out.print("Username: ");
        String uname=new Scanner(System.in).nextLine();
        Client client=new Client(uname);
        boolean success=client.connectToServer(args[0],args[1]);
        if(!success){
            System.out.println("Not able to connect.");
            return;
        }
        client.startChat();
    }
}
