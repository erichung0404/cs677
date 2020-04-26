package io.rmi.demo;

public class Peer1 {
    public Peer1() {
        Thread server = new Thread(new Server(8000, "Subtractor"));
        //Thread client = new Thread(new Client(8001, "Adder"));
        server.start();
        //client.start();
    }

    public static void main(String[] args) {
        Peer1 peer = new Peer1();
    }
}
