package io.rmi.demo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server implements Runnable {
    private Peer peer = null;

    @Override
    public void run() {

    }

    public Server(int port, String name) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            switch(name) {
                case "Subtractor":
                    peer = new SubtractorImpl();
                    break;
                case "Adder":
                    peer = new AdderImpl();
                    break;
                default:
                    System.out.println("Can't find " + name);
            }
            registry.rebind(name, peer);
            System.out.println("Server is ready for " + name);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
