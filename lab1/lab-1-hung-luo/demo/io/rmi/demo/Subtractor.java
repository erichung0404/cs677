package io.rmi.demo;

import java.rmi.RemoteException;

public interface Subtractor extends Peer {
    public int service(int a, int b) throws RemoteException;
    public int subtract(int a, int b) throws RemoteException;
    public int multiply(int a, int b) throws RemoteException;
}
