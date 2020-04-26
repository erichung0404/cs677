package com.dslab2;
import static spark.Spark.*;
import java.util.*;
import com.google.gson.*;

public class CatelogService {

    public CatelogService(CatelogServer clServer){

        port(clServer.ip.getPort());

        get("querySubject/:subject", (req, res) ->{
            String subject = req.params(":subject");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( clServer.querySubject(subject) );
        });

        get("queryItem/:itemNumber", (req, res) ->{
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( clServer.queryItem(itemNumber) );
        });

        get("requestItem/:itemNumber", (req, res) ->{
            int itemNumber = Integer.parseInt(req.params(":itemNumber"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( clServer.requestItem(itemNumber) );
        });

        post("reloadItem/", (req, res) ->{
            int itemNumber = Integer.parseInt(req.queryParams("itemNumber"));
            int reloadAmount = Integer.parseInt(req.queryParams("reloadAmount"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( clServer.reloadItem( itemNumber
                                                        ,  reloadAmount ) );
        });

        post("updateCost/", (req, res) ->{
            int itemNumber = Integer.parseInt(req.queryParams("itemNumber"));
            int cost = Integer.parseInt(req.queryParams("cost"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( clServer.updateCost(itemNumber
                                                        , cost) );
        });

    }



    public static void main(String[] args) throws Exception {

        // Check if enter all ip address
        if(args.length < 5){
            System.out.println("Please enter FrontEnd, Catelog and Order IPaddrss:port, logFileURL");
            return;
        }

        CatelogServer clServer = new CatelogServer()
                .withSelfIP(args[1])
                .withFrontEndIP(args[0])
                .withOrderIP(args[2])
                .withDataURL(args[3])
                .withLogFileURL(args[4]);

        new CatelogService(clServer);

        System.out.println("Catelog Service Running");
    }
}