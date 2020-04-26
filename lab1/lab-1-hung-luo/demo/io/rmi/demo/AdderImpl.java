package io.rmi.demo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AdderImpl extends UnicastRemoteObject implements Adder {
    public AdderImpl() throws RemoteException {
        super();
    }

    @Override
    public int service(int a, int b) throws RemoteException {
        return add(a, b);
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }

    @Override
    public int multiply(int a, int b) throws RemoteException {
        return a*b;
    }
}
