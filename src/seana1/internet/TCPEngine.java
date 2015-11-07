package seana1.internet;

import seana1.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;

/**
 * Created by bensoer on 03/11/15.
 */
public class TCPEngine {


    private Socket clientSocket;

    private ServerSocket serverSocket;
    private Socket serverSessionSocket;
    //private PrintWriter out;
    private ObjectOutputStream out;
    //private BufferedReader in;
    private ObjectInputStream in;

    public TCPEngine(){

    }

    public void createClientSocket(String hostName, int portNumber) throws UnknownHostException, IOException{
        System.out.println("TCPEngine - Attempting to connect to " + hostName + " on port " + portNumber);
        try{
            System.out.println("TCPEngine - Creating Client input and output socket buffers");
            clientSocket = new Socket(hostName, portNumber);
            //out = new PrintWriter(clientSocket.getOutputStream(), true);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            //in = new BufferedReader(
                   // new InputStreamReader(clientSocket.getInputStream()));
        }catch(UnknownHostException uhe){
            System.out.println("TCPEngine - Failed to Create Socket");
            uhe.printStackTrace();
            throw uhe;
        }catch(IOException ioe){
            System.out.println("TCPEngine - Failed to Access IO");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public void createServerSocket(int portNumber) throws IOException{
        System.out.println("TCPEngine - Attempting to Create Server Socket on port " + portNumber);
        if(serverSocket != null && !serverSocket.isClosed()){
            closeSocket();
        }

        try{
            serverSocket = new ServerSocket(portNumber);
        }catch(IOException ioe){
            System.out.println("Unable to Create Server Socket. Could not Allocate Resources");
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public String startSession() throws NullPointerException, IOException{
        System.out.println("TCPEngine - Starting A Session");
        if(serverSocket == null){
            throw new NullPointerException("Server Socket Must Be Initialized before starting a session");
        }

        try{
            System.out.println("TCPEngine - Awaiting A Connection Request");
            serverSessionSocket = serverSocket.accept();
            System.out.println("TCPEngine - Allocating Input and Output Buffers for New Connection Request");
            //out = new PrintWriter(serverSessionSocket.getOutputStream(), true);
            out = new ObjectOutputStream(serverSessionSocket.getOutputStream());
            in = new ObjectInputStream(serverSessionSocket.getInputStream());
            //in = new BufferedReader(
              //      new InputStreamReader(serverSessionSocket.getInputStream()));
            return serverSessionSocket.getInetAddress().toString();
        }catch(IOException ioe){
            System.out.println("Server Could Not Start Session, Unable to Allocate");
            ioe.printStackTrace();
            throw ioe;
        }




    }

    public void writeToSocket(Packet packet){
        try{
            out.writeObject(packet);
            out.flush();
        }catch(IOException ioe){
            System.out.println("Failure Serielizing to Socket");
            ioe.printStackTrace();
        }

        //out.print(message + "DoNE!");
        //out.flush();
    }



    public void closeSocket(){
        if(serverSessionSocket != null && !serverSessionSocket.isClosed()){
            try{
                serverSessionSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Server Session Socket");
            }

        }

        if(clientSocket != null && !clientSocket.isClosed()){
            try{
                clientSocket.close();
            }catch(IOException ioe){
                System.out.println("Failed to Close the Client Socket");
            }
        }
    }

 /*   public String readFromSocket() throws IOException{
        try{
            String fullMessage = null;
            String userInput;
            final int BUFFER = 2048;
            char[] buffer = new char[BUFFER];
            //System.out.println(in.read(buffer));
            while(in.read(buffer) != -1){
                System.out.println("Reading Content");
                System.out.println(buffer);
                System.out.println(new String(buffer));
                System.out.println("Checking if has a DoNE!");
                if(this.isEnd(buffer)){
                    System.out.println("This one has a done");
                    int index = getIndexOfEnd(buffer);

                    String strBuffer = new String(buffer);
                    String subBuffer = strBuffer.substring(0, index);


                    if(fullMessage == null){
                        fullMessage = subBuffer;
                    }else{
                        fullMessage += subBuffer;
                    }

                    System.out.println("Returning from read");
                    return fullMessage;

                }else{
                    System.out.println("Content has no done");
                    if(fullMessage == null){
                        fullMessage = new String(buffer);
                    }else{
                        fullMessage += new String(buffer);
                    }
                    System.out.println("Appending and cycling again");
                }
                buffer = new char[BUFFER];
            }
            return null;
            /*while((userInput = in.readLine()) != null){
                if(fullMessage == null){
                    fullMessage = userInput;
                }else{
                    fullMessage += userInput;
                }
            }*//*

            //return fullMessage;
        }catch(IOException ioe){
            System.out.println("Error Reading Content");
            ioe.printStackTrace();
            throw ioe;
        }

    }
*/
    public Packet readFromSocket(){
        Object result = null;
        try {
            result = in.readObject();

        }catch(IOException ioe){
            System.out.println("IOException reading packet from socket");
            ioe.printStackTrace();

        }catch(ClassNotFoundException cnfe){
            System.out.println("ClassNotFoundException reading packet from socket");
            cnfe.printStackTrace();
        }
        return (Packet)result;
    }

    private boolean isEnd(char[] buffer){
        String strBuffer = new String(buffer);

        System.out.println("In is end. buffer pre convert: " + buffer);
        System.out.println("In is end. The buffer: " + strBuffer);


        int index = strBuffer.indexOf("DoNE!");
        System.out.println(index);

        return index > -1;
    }

    private int getIndexOfEnd(char[] buffer){
        return new String(buffer).indexOf("DoNE!");
    }


}
