import java.io.*;
import java.rmi.Naming;
import java.util.*;
import java.net.*;

public class Peer {
    protected final int type;    // 0=Buyer   1=Seller    2=BuyerAndSeller
    protected final IP ip;
    protected int peerID;
    protected final List<Integer> neighborPeerID;
    protected final Map<Integer, IP> peerIDIPMap;    // neighborPeerID   ->    neighborIP

    public static double avgRMICallDuration = 0;

    public Peer(int peerID, int peerType, IP ip, List<Integer> neighborPeerID, Map<Integer, IP> peerIDIPMap){
        this.type = peerType;
        this.peerID = peerID;
        this.ip = ip;
        this.neighborPeerID = neighborPeerID;
        this.peerIDIPMap = peerIDIPMap;
        System.out.println("Peer " + this.peerID + " initiated, type " + this.type);
    }

    public void handleMessage(Message m){
        switch (m.getOperationType()){
            case LOOKUP:
                handleLookUp(m);
                break;
            case REPLY:
                handleReply(m);
                break;
            case BUY:
                handleBuy(m);
                break;
        }
    }

    protected void handleLookUp(Message m) {};
    protected void handleReply(Message m) {};
    protected void handleBuy(Message m) {};


    // When Seller send "REPLY" back to buyer, mid nodes need to pass message backward the routepath
    public void backward(Message m){
        IP prevIP = m.routePathPopRear();
        /*
        System.out.println("Peer " + this.peerID +
                " Backward Message " + m.getOperationType() +
                " to " + prevIP.toString());
        */
        sendMessage(m, prevIP);
    }

    // Spread message to all its neighbor
    public void spread(Message m){
        /*
        System.out.println("Peer " + this.peerID +
                " Spread Message " + m.getOperationType());
        */
        try {
            for (int peerID : peerIDIPMap.keySet()) {
                IP destIP = peerIDIPMap.get(peerID);
                sendMessage(m, destIP);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(Message m, IP desIP){
        String RMIName = "RMIserver";
        /*
        System.out.println("Message id:" + m.getID() +
                           " Message Type: " + m.getOperationType +
                           " from " + this.ip.toString() +
                           " to " + desIP.toString());
        */
        try {

            //long TimeBeforeRMICall = System.currentTimeMillis();

            String desHost = desIP.getAddr();
            int desPort = desIP.getPort();
            RemoteInterface serverFunction = (RemoteInterface) Naming.lookup("//" + desHost + ":" + desPort  + "/" + RMIName);

            serverFunction.send(m);

            // RMI Call Performance calculation
            /*
            long TimeAfterRMICall = System.currentTimeMillis();
            long RMICallDuration = TimeAfterRMICall - TimeBeforeRMICall;
            if (avgRMICallDuration == 0){
                avgRMICallDuration = RMICallDuration;
            }
            else
                avgRMICallDuration = (double) ((avgRMICallDuration * 9 + RMICallDuration)/10);
            System.out.println( "Duration = " + RMICallDuration  );
            System.out.println( "Average RMICallDuration = " + avgRMICallDuration  );
            */

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


}