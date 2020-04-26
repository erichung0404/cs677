package io.rmi.demo;

public class Peer2 {
    public Peer2() {
        //Thread server = new Thread(new Server(8001, "Adder"));
        Thread client = new Thread(new Client(8000, "Subtractor"));
        //server.start();
        client.start();
    }

    public static void main(String[] args) {
        Peer2 peer = new Peer2();
    }
}
