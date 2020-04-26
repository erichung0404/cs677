package io.rmi.demo;

import java.rmi.RemoteException;

public interface Adder extends Peer {
    public int service(int a, int b) throws RemoteException;
    public int add(int a, int b) throws RemoteException;
    public int multiply(int a, int b) throws RemoteException;
}
