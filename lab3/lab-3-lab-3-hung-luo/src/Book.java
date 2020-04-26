package com.dslab2;
import static spark.Spark.*;
import java.util.*;

public class Book {
    public int itemNumber;
    public String name;
    public String topic;
    public int stack;
    public int cost;

    public Book(){
    }

    public Book(int itemNumber, String name, String topic, int stack, int cost ){
        this.itemNumber = itemNumber;
        this.name = name;
        this.topic = topic;
        this.stack = stack;
        this.cost = cost;
    }

    public Book withStringData(String s){
        String [] data = s.split(",");
        this.itemNumber = Integer.parseInt(data[0]);
        this.name = data[1];
        this.topic = data[2];
        this.stack = Integer.parseInt(data[3]);
        this.cost = Integer.parseInt(data[4]);
        return this;
    }

    public String toString(){
        return "" + itemNumber + "," + name + "," + topic + "," + stack + "," + cost;
    }

}