package com.dslab2;
import static spark.Spark.*;
import java.net.*;
import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.*;
import org.apache.http.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class FrontEndService {

    public FrontEndService(com.dslab2.FrontEndServer feServer){
        port(feServer.ip.getPort());


        get("search/:topic", (req, res) -> {
            String topic = req.params(":topic");
            return new Gson().toJson( feServer.search(topic) );
        });

        get("lookup/:itemNumber", (req, res) -> {
            Integer itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return new Gson().toJson( feServer.lookUp(itemNumber) );
        });

        get("buy/:itemNumber", (req, res) -> {
            Integer itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return new Gson().toJson( feServer.buy(itemNumber) );
        });

        delete("invalidate/:itemNumber", (req, res) -> {
            Integer itemNumber = Integer.parseInt(req.params(":itemNumber"));
            return new Gson().toJson( feServer.invalidate(itemNumber) );
        });
    }

    public static void main(String[] args) throws Exception {
        // Check if enter all ip address
        if(args.length < 3){
            System.out.println("Please enter FrontEnd, Catelog and Order IPaddrss:port");
            return;
        }

        com.dslab2.FrontEndServer feServer = new com.dslab2.FrontEndServer()
                       .withSelfIP(args[0])
                       .withCatelogIP(args[1])
                       .withOrderIP(args[2])
                       .withLogFileURL(args[3]);

        new FrontEndService(feServer);
        System.out.println("Front End Service Running");
    }

}