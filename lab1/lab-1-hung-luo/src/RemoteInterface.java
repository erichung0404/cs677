import java.io.*;
import java.util.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface RemoteInterface extends Remote {
    public void send(Message m) throws RemoteException;
}