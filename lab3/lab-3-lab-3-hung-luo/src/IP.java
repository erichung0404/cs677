package com.dslab2;
import java.io.*;
import java.util.*;
import java.net.*;

public class IP{
    private String addr;
    private int port;

    public IP(){
    }

    public IP withString(String s){
        parseIPString(s);
        return this;
    }

    public IP(String addr, int port){
        this.addr = addr;
        this.port = port;
    }

    public String getAddr() {
        return addr;
    }

    public int getPort(){
        return port;
    }

    public String toString(){
        return "" + addr + ":" + Integer.toString(port);
    }

    public boolean equals(IP anotherIP){
        if(anotherIP == null){
            return false;
        }
        if( this.toString().equals(anotherIP.toString()) ){
            return true;
        }
        return false;
    }

    private IP parseIPString(String s){
        String[] temp = s.split(":");
        this.addr = temp[0];
        this.port = Integer.valueOf(temp[1]);
        return this;
    }
}