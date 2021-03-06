package networkemulator.server;

import networkemulator.*;
import networkemulator.socketemulator.TCPEngine;
import networkemulator.socketemulator.WindowManager;

/**
 * Created by bensoer on 03/11/15.
 *
 * Server is the main entrance point for the Server module of the network emulator. It sets up and initializes its 2
 * threads for recieving initialy data from the client, and then sending data back to the client. This class then
 * holds onto the threads and terminates them when the Server class is terminated.
 */
public class Server {

    private static TCPEngine manager = new TCPEngine();
    private static WindowManager wm;

    public static boolean canSendBack = false;

    public static void main(String[] args){
        ConfigurationManager cm = ConfigurationManager.getInstance();
        wm = new WindowManager(cm.serverConnectionWindowSize, cm.serverConnectionInitTimeout);
        Logger.configure(true,true, "./ServerLog.txt");
        try{
            manager.createServerSocket(cm.serverConnectionPort);
            Logger.log("Server - Server Created");
            manager.startSession();
            Logger.log("Server - Connection Accepted");
            //manager.closeSocket();

        }catch(Exception e){
            e.printStackTrace();
        }

        Logger.log("Server - Preparing Sender Thread");
        Thread sss = new ServerSocketSender(manager, wm);
        Logger.log("Server - Prepped Sender Thread");

        Logger.log("Server - Creating Listener Thread");
        Thread sln = new ServerSocketListener(manager, wm, sss);
        sln.start();

        Logger.log("Server - Listening Thread Created");


        try{
            sln.join();
            sss.join();
        }catch(InterruptedException ie){
            Logger.log("Server - Interrupt Exception Joing Thread to Main Thread");
        }
    }




}
