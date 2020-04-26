package com.dslab2;
import static spark.Spark.*;
import java.util.*;
import com.google.gson.*;

public class OrderService {

    public OrderService(OrderServer oServer){
        port(oServer.ip.getPort());

        get("buy/:itemNumber", (req, res) -> {
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return new Gson().toJson( oServer.buy(itemNumber) );
        });

    }

    public static void main(String[] args) throws Exception {

        // Check if enter all ip address
        if(args.length < 4){
            System.out.println("Please enter FrontEnd, Catelog and Order IPaddrss:port");
            return;
        }

        OrderServer oServer = new OrderServer()
                .withSelfIP(args[2])
                .withFrontEndIP(args[0])
                .withCatelogIP(args[1])
                .withOrderHistoryURL(args[3]);

        new OrderService(oServer);

        oServer.reload(0,5);

        System.out.println("Order Service Running");
    }
}