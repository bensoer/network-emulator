package networkemulator.internet;

import networkemulator.*;

import java.io.IOException;
import java.util.Random;

/**
 * Created by bensoer on 03/11/15.
 */
public class Internet {


    private TCPEngine listener = new TCPEngine();
    private TCPEngine sender = new TCPEngine();

    private boolean isClientsTurn = true;

    private Random generator = new Random();

    private int bitErrorPercent;

    public Internet(int bitErrorPercent){
        this.bitErrorPercent = bitErrorPercent;
    }

    public void startInternet(){
        Logger.configure(true, true, "./InternetLog.txt");

        //setup listener for incoming client connections
        try{
            listener.createServerSocket(8000);
        }catch(IOException ioe){
            System.out.println("Failed to Setup Listener's Resources");
            ioe.printStackTrace();
        }

        //setup sender to connect with the server
        try{
            sender.createClientSocket("localhost", 7000);
        }catch(IOException ioe){
            System.out.println("Failed to Allocated Sender's Recources");
            ioe.printStackTrace();
        }

        //starts the listener to look for incoming client requests
        startSession();

        //start the threads for listening on both inputs at the same time
        Thread c2i = new Client2Internet(listener, sender);
        Thread s2i = new Server2Internet(sender, listener);

        c2i.start();
        s2i.start();

        try {
            c2i.join();
            s2i.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startSession(){
        try{
            System.out.println("Internet - Waiting for Connections from Client");
            String clientAddress = this.listener.startSession();
            System.out.println("Internet - Accepted Connection from Client " + clientAddress);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }




}