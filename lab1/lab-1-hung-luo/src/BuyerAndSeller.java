import java.io.*;
import java.net.InetAddress;
import java.rmi.Naming;
import java.util.*;


public class BuyerAndSeller extends Peer {

    // Buyer Part
    private int currentRequestMessageID = 0;
    private HashSet<Message> replyPool = new HashSet<>();   // Record all replys from other sellers

    // Seller Part
    private int productType;        // Integer in range [0,2]
    private int stock = 0;
    private HashMap<Integer, Message> holdSet = new HashMap<Integer, Message>();   // After received "LOOKUP" message, seller hold product and waiting "BUY" message
                                 // Seller will discard message after messages' life time (for example, 100ms)

    public BuyerAndSeller(int peerID, int peerType, IP ip, List<Integer> neighborPeerID, Map<Integer, IP> peerIDIPMap ){
        super(peerID, peerType, ip, neighborPeerID, peerIDIPMap);
        new LookUpThread().start();

        checkAndIniStock();
        holdSet = new HashMap<Integer, Message>();
        new StockAndHoldSetCheckThread().start();
    }

    public class LookUpThread extends Thread{
        public void run() {
            while(true){
                try {
                    LookUp();
                    Thread.sleep(Node.INTERVAL_TIME);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
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

    // Same as seller handle LookUp
    protected void handleLookUp(Message m) {
        // dont spread if node in the path
        if( m.getBuyerPeerID() == this.peerID )
            return;
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
                holdSet.put(m.getID(), m);
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

    // Same as Buyer handleReply
    protected void handleReply(Message m) {
        if(peerID == m.getBuyerPeerID()) { // initial buyer
            if (currentRequestMessageID == m.getID()){
                synchronized(replyPool){
                    replyPool.add( m );
                    return;
                }
            }
        } else { // mid node
            backward(m);
        }

    }

    // Same as Seller handleReply
    protected void handleBuy(Message m) {
        System.out.println( "Peer " + this.peerID +
                " received BUY from peer " + m.getBuyerPeerID() +
                " for product " + m.getItemType() +
                " and Sold it" +
                " MessageID " + m.getID() );
        holdSet.remove(m.getID());
        stock--;
    }

    // Almost the same as Buyer LookUp
    // But dont LookUp product that itself sells
    private void LookUp(){
        // Create Message
        Message m = new Message().withOperationType(Message.Operation.LOOKUP)
                .withBuyerPeerID(this.peerID)
                .withBuyerIP(this.ip)
                .withItemType( pickItemTypeToLookUp() );
        m.routePathAddRear(this.ip);
        m.hopAddOne();

        currentRequestMessageID = m.getID();
        replyPool = new HashSet<>();
        new latencyBuyThread().start();

        System.out.println( "Buyer " + this.peerID +
                " LookUp product " + m.getItemType() +
                " MessageID " + m.getID() );
        spread(m);
    }

    private int pickItemTypeToLookUp(){
        int ItemType = Math.abs(new Random().nextInt()%3);
        while( ItemType == this.productType ){
            ItemType = Math.abs(new Random().nextInt()%3);
        }
        return ItemType;
    }

    // Same as Buyer latencyBuyThread
    // after sending out LOOKUP over a specific amount of time, attemp to BUY it.
    private class latencyBuyThread extends Thread{

        public latencyBuyThread(){
        }
        public void run() {
            try {
                Thread.sleep( (int)(Node.INTERVAL_TIME)/2 );
                sendBuyMessage();
            }catch(InterruptedException ie){
                System.out.println(ie);
            }
        }
    }

    // Same as Buyer sendBuyMessage
    private void sendBuyMessage(){
        Message buyM;
        synchronized(replyPool) {
            buyM = randomPickSeller();
        }
        if(buyM == null){
            System.out.println("No Seller found!" );
            return;
        }
        else{
            buyM = buyM.withOperationType(Message.Operation.BUY);
            sendMessage(buyM, buyM.getSellerIP());
            System.out.println( "Peer " + this.peerID +
                    " BUY product " + buyM.getItemType() +
                    " from Peer " + buyM.getSellerPeerID());
        }
    }

    // Same as Buyer randomPickSeller
    private Message randomPickSeller(){
        if(replyPool.size() > 0){
            Iterator<Message> itr = replyPool.iterator();
            if(itr.hasNext()){
                Message m = itr.next();
                return m;
            }
        }
        return null;
    }

    // Same as Seller cleanHoldSet
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

    // Same as Seller checkAndIniStock
    private void checkAndIniStock(){
        if (stock == 0){
            productType = Math.abs(new Random().nextInt()%3);
            stock = new Random().nextInt(5);
        }
    }

}