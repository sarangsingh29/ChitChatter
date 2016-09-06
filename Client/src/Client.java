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

    public void sendFile(String fileName){
        try{
            /*
                Maverick: Thread is made to sleep so that the file contents are not
                sent as a normal message along with the file-send command.
             */
            Thread.currentThread().sleep(1000);
            System.out.println("File sending started.");
            File file=new File(fileName);
            InputStream fileStream=new FileInputStream(file);
            byte[]fileData=new byte[1024];
            while(true){
                int readData=fileStream.read(fileData);
                clientSocket.getOutputStream().write(fileData,0,readData);
                if(readData<1024)
                    break;
            }
            System.out.println("File sent.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void receiveFile(String outFile){
        try{
            System.out.println("File receiving started.");
            File file=new File(outFile);
            if(!file.exists()){
                file.createNewFile();
                System.out.println("New file created.");
            }
            FileOutputStream fileOutStream=new FileOutputStream(file);
            byte[] data=new byte[1024];
            while(true){
                int readLen=clientSocket.getInputStream().read(data);
                fileOutStream.write(data,0,readLen);
                if(readLen<1024)
                    break;
            }
            System.out.println("File received. Saved as: "+ outFile);
        }
        catch(Exception e){
            e.printStackTrace();
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
                    String stringMsg=new String(msg,0,msgLen-1);

                    if(stringMsg.compareTo("clear")==0){
                        System.out.println("\n\n\n\n\n");
                        continue;
                    }

                    String[]splittedMsg=stringMsg.split(":");
                    if(splittedMsg.length>=2 && splittedMsg[1].split(" ")[0].compareTo("file")==0){
                        sendFile(splittedMsg[1].split(" ")[1]);
                    }

                    if(new String(msg,0,msgLen-1).compareTo("stop")==0){
                        System.out.println("Going offline.");
                        break;
                    }
                }
                if(socketIn.available()!=0){
                    msgLen=socketIn.read(msg);
                    System.out.println(new String(msg,0,msgLen));
                    String[]splittedMsg=new String(msg,0,msgLen).split(":");
                    if(splittedMsg.length>=2 && splittedMsg[1].contains("file")){
                        System.out.print("Output File Name: ");
                        receiveFile(new Scanner(System.in).nextLine());
                    }
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
