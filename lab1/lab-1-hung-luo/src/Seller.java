import java.io.*;
import java.net.InetAddress;
import java.rmi.Naming;
import java.util.*;


public class Seller extends Peer {

    private int productType;        // Integer in range [0,2]
    private int stock = 0;
    private HashMap<Integer, Message> holdSet = new HashMap<Integer, Message>();   // After received "LOOKUP" message, seller hold product and waiting "BUY" message
                                                 // Seller will discard message after messages' life time (for example, 100ms)

    public Seller(int peerID, int peerType, IP ip, List<Integer> neighborPeerID, Map<Integer, IP> peerIDIPMap ){
        super(peerID, peerType, ip, neighborPeerID, peerIDIPMap);
        checkAndIniStock();
        holdSet = new HashMap<Integer, Message>();

        new StockAndHoldSetCheckThread().start();

    }

    public class StockAndHoldSetCheckThread extends Thread{
        public void run() {
            while(true){
                cleanHoldSet();
                checkAndIniStock();
                try {
                    Thread.sleep((int)(Node.INTERVAL_TIME/6));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    protected void handleLookUp(Message m) {
        // dont spread if node in the path
        for(IP routeIP: m.getRoutePath()) {
            if(ip.getAddr().equals(routeIP.getAddr()) && ip.getPort() == routeIP.getPort()) {
                return;
            }
        }

        // dont spread if step over MAX_HOP
        if( m.getHop() >= m.MAX_HOP ){
            return;
        }
        m.hopAddOne();

        if ( m.getItemType() == productType && stock > holdSet.size() ){    // can provide goods
            if ( holdSet.containsKey(m.getID()) ) { // Replyed before
                return;
            }
            else{   // new lookup message
                System.out.println( "Peer " + this.peerID +
                                    " Reply LookUp from peer " + m.getBuyerPeerID() +
                                    " for product " + m.getItemType() +
                                    " MessageID " + m.getID() );
                m.startHold(System.currentTimeMillis());
                synchronized (holdSet){
                    holdSet.put(m.getID(), m);
                }
                // send "REPLY" message
                m = m.withOperationType(Message.Operation.REPLY)
                        .withSellerPeerID(peerID)
                        .withSellerIP(ip);
                backward(m);
            }
        }
        else {      // cannot provide goods
            /*
            System.out.println( " Peer " + this.peerID +
                                " Spread LookUp from " + m.getBuyerPeerID() +
                                " for " + m.getItemType() +
                                " MessageID " + m.getID() );
            */
            m.routePathAddRear(this.ip);
            spread(m);
        }
    }

    protected void handleReply(Message m) {
        backward(m);
    }

    protected void handleBuy(Message m) {
        System.out.println( "Peer " + this.peerID +
                " received BUY from peer " + m.getBuyerPeerID() +
                " for product " + m.getItemType() +
                " and Sold it" +
                " MessageID " + m.getID() );
        holdSet.remove(m.getID());
        stock--;
    }

    // Delete messages that out of life time
    private void cleanHoldSet(){
        Iterator<Map.Entry<Integer, Message>> iterator = holdSet.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Message> pair = (Map.Entry<Integer, Message>)iterator.next();
            Message m = pair.getValue();
            if ( m.outDate(System.currentTimeMillis()) ) {
                iterator.remove();
            }
        }
    }

    private void checkAndIniStock(){
        if (stock == 0){
            productType = Math.abs(new Random().nextInt()%3);
            stock = new Random().nextInt(5);
        }
    }

}