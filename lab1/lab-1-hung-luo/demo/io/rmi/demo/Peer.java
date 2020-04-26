package io.rmi.demo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Peer extends Remote {
    public int service(int a, int b) throws RemoteException;
    public int multiply(int a, int b) throws RemoteException;
}
