/**
 * Created by Maverick on 5/9/16.
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ChatThread extends Thread{
    Server server;
    Socket socket;
    String uname;
    public ChatThread(Server server, Socket socket, String uname){
        this.server=server;
        this.socket=socket;
        this.uname=uname;
    }
    public void run()
    {
        try{
            InputStream socketIn=socket.getInputStream();
            OutputStream socketOut=socket.getOutputStream();

            byte[] byteMsg = new byte[1000];

            while(true){
                    if(socketIn.available()!=0) {
                        int msgLen=socketIn.read(byteMsg);
                        String stringMsg = new String(byteMsg,0,msgLen);
                        System.out.println(uname+"#"+stringMsg);
                        if(stringMsg.compareTo("stop")==0){
                            stopChat();
                            break;
                        }
                        else if(stringMsg.compareTo("refresh")==0){
                            refreshList();
                            continue;
                        }
                        String[] splittedMsg = stringMsg.split(":");
                        if(splittedMsg.length!=2){
                            System.out.println("Unknown message format.");
                            socket.getOutputStream().write(new String("Unknown format").getBytes());
                            continue;
                        }

                        Socket outSocket = server.find(splittedMsg[0]);
                        if (outSocket != null) {
                            outSocket.getOutputStream().write((uname + ":" + splittedMsg[1]).getBytes());
                            if(splittedMsg[1].contains("file")){
                                handleFile(socketIn,outSocket.getOutputStream());
                            }
                        }
                        else{
                            System.out.println("Unknown recipient.");
                            socket.getOutputStream().write(new String("Unknown client").getBytes());
                        }
                    }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void handleFile(InputStream socketIn, OutputStream outSocket){
        try{
            System.out.println("File handling started.");
            byte[] data=new byte[1024];
            while(true){
                int dataLen=socketIn.read(data);
                outSocket.write(data,0,dataLen);
                //System.out.println(new String(data,0,dataLen));
                if(dataLen<1024)
                    break;
            }
            System.out.println("File Handled.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopChat(){
        System.out.println("Bye "+uname);
        int i=0;
        boolean removed=false;
        while(!removed) {
            try {
                for (Client client : server.connectedClients) {
                    if (client.uname.equals(this.uname)) {
                        server.connectedClients.remove(i);
                        removed = true;
                    }
                    i++;
                }
            }
            /*
                Maverick: Keep handling exceptions until the client is removed.
             */
            catch (ConcurrentModificationException e) {
                removed=false;
            }
            catch(Exception e){
                removed=false;
            }
        }
    }

    public void refreshList(){
        server.displayActiveClients(socket);
    }
}
