package io.rmi.demo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SubtractorImpl extends UnicastRemoteObject implements Subtractor {
    public SubtractorImpl() throws RemoteException {
        super();
    }

    @Override
    public int service(int a, int b) throws RemoteException {
        return subtract(a, b);
    }

    @Override
    public int subtract(int a, int b) throws RemoteException {
        return a - b;
    }
    
    @Override
    public int multiply(int a, int b) throws RemoteException {
        return a*b;
    }
}
