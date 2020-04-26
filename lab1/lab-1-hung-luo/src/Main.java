import java.io.*;
import java.util.*;
import java.lang.*;

public class Main{
    public static void main(String[] args)throws IOException, InterruptedException{
        // args[0] is the address of config file
        String fileAddress = args[0];
        int                   peerID          = Integer.parseInt(args[1]);
        int                   nodeType        = 0;   // Buyer=0  Seller=1  BuyerAndSeller=2
        IP                    nodeIP          = null;
        List<Integer>         neighborPeerID  = new ArrayList<Integer>();
        Map<Integer, IP>  peerIPMap       = new HashMap<Integer, IP>();

        // Read configuration file
        FileReader file = new FileReader(fileAddress);
        BufferedReader br = new BufferedReader(file);
        String line;
        while( (line = br.readLine())!= null){
            String[] params = line.split(" ");
            // Only Initialize specific Node in this terminal
            if( peerID != Integer.parseInt(params[0]) ){
                continue;
            }

            // Get Parameters from configuration file
            nodeType                      = Integer.parseInt(params[1]);
            nodeIP                        = new IP().withString(params[2]);
            String[] inputNeighborPeerIDs = params[3].split(",");
            String[] inputNeighborIPString     = params[4].split(",");

            for(int i=0; i<inputNeighborPeerIDs.length; i++){
                neighborPeerID.add(Integer.parseInt(inputNeighborPeerIDs[i]));
                peerIPMap.put(Integer.parseInt(inputNeighborPeerIDs[i]), new IP().withString(inputNeighborIPString[i]) );
            }
        }
        br.close();
        file.close();

        // Create Nodes and Run Nodes
        //myPeerID, NodeType, myIP, neighborPeerID, peerIPMap
        System.out.println("PeerID " + peerID);
        System.out.println("NodeType = "+nodeType);
        System.out.println("myIP = "+nodeIP.toString());
        System.out.println("neighborPeerID:");
        System.out.println(neighborPeerID);
        System.out.println("peerIPMap:");
        System.out.println(peerIPMap);

        // Initialize Node based on its types
        Node node = new Node(peerID, nodeType, nodeIP, neighborPeerID, peerIPMap);
        node.run();

        // Define Nodes as Buyer, Seller or Both
        System.out.print("Initialization Finished, Node Running\n");

        long timeNow = System.currentTimeMillis();
        System.out.print("time now:" + timeNow);
    }

}