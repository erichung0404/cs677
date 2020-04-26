package io.rmi.demo;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;

public class Client implements Runnable{
    @Override
    public void run() {

    }

    public Client(int port, String name) {
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Requesting " + name);
            try {
                Thread.sleep(5000);
                String host = InetAddress.getLocalHost().getHostAddress();
                Peer peer = (Peer) Naming.lookup("//" + host + ":" + port + "/" + name);
                System.out.println("Enter a: ");
                int a = in.nextInt();
                System.out.println("Enter b: ");
                int b = in.nextInt();
                System.out.println("Result: " + peer.multiply(a, b));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
