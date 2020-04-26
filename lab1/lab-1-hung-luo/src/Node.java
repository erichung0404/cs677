import java.io.*;
import java.util.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Node implements Runnable {
    private Peer peer;

    protected final IP ip;
    protected final int peerID;

    private BlockingQueue<Message> messageQueue;

    public static int INTERVAL_TIME =  5000;

    public Node(int peerID, int peerType, IP ip, List<Integer> neighborPeerID, Map<Integer, IP> peerIDIPMap){
        this.peerID = peerID;
        this.ip = ip;
        this.messageQueue = new LinkedBlockingQueue<>();
        peerGeneration(peerID, peerType, ip, neighborPeerID, peerIDIPMap);

        new MessageQueueCheckThread().start();

        new Thread(new RMIServerThread(ip)).start();
    }

    public void run(){

    }


    // For each node, check its message constantly
    public class MessageQueueCheckThread extends Thread{
        public void run(){

            while(true){
                checkMessageQueue();
            }

        }
    }


    // implement it with rmi
    // functions to add Message into messageQueue
    public class RMIServerThread implements Runnable{
        private IP my_ip;
        private String name = "RMIserver";

        public RMIServerThread(IP ip){
            this.my_ip = ip;

            try {
                Registry registry = LocateRegistry.createRegistry(ip.getPort());
                ServerFunction serverInstance = new ServerFunction();
                registry.rebind(name, serverInstance);
                System.out.println("Server is ready for " + name);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run(){
            System.out.println("ServerThread Running" + peer.type);

        }
    }

    public class ServerFunction extends UnicastRemoteObject implements RemoteInterface {
        public ServerFunction() throws RemoteException {
            super();
        }

        @Override
        public void send(Message m) throws RemoteException {
            messageQueue.add(m);
        }
    }


    // generate peer based on its type
    private void peerGeneration(int peerID, int peerType, IP ip, List<Integer> neighborPeerID, Map<Integer, IP> peerIDIPMap){
        if(peerType == 0){       // Buyer
            peer = new Buyer(peerID, peerType, ip, neighborPeerID, peerIDIPMap);
        }
        else if(peerType == 1) { //Seller
            peer = new Seller(peerID, peerType, ip, neighborPeerID, peerIDIPMap);
        }
        else{                    // 3: BuyerAndSeller
            peer = new BuyerAndSeller(peerID, peerType, ip, neighborPeerID, peerIDIPMap);
        }
    }

    // If there is a message, then handle it
    // to prevent problems in paralle computing, we serialize message
    private void checkMessageQueue(){
        /*
        synchronized (messageQueue){
            if( messageQueue.isEmpty() != true){
                peer.handleMessage( messageQueue.poll() );
                messageQueue.notifyAll();
            }
        }
        */
        try{
            peer.handleMessage(messageQueue.take());
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


}