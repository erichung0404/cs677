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

public class OrderServer {
    public IP ip;
    private IP frontEndIP;
    private IP catelogIP;
    private final int RELOAD_TIME = 50000;

    private String orderHistoryURL;

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public OrderServer(){
        new ReloadThread().start();
    }

    public String buy(int itemNumber) throws Exception{
        String reqResult ="";
        String displayResult = "";
        try {
            HttpGet request = new HttpGet("http://" + catelogIP.toString() + "/requestItem/"+itemNumber);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                reqResult = EntityUtils.toString(entity);
            }

            if( reqResult.contains("true") ){
                displayResult = "buy " + itemNumber +" success";
            } else{
                displayResult = "buy " + itemNumber +" fail";
            }
            System.out.println(displayResult);
            writeIntoFile(displayResult);
            return reqResult;
        } catch (Exception e){
            displayResult = "buy " + itemNumber +" exception";
            System.out.println(displayResult);
            writeIntoFile(displayResult);
            return reqResult;
        }
    }

    public class ReloadThread extends Thread{
        public void run() {
            while(true){
                try {
                    Thread.sleep(RELOAD_TIME);
                    // only reload item 0 and item 1
                    reload(0,5);
                    reload(1,5);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    public void reload(int itemNumber, int reloadAmount) throws Exception{
        String reqResult ="";
        String displayResult = "";

        HttpPost post = new HttpPost("http://" + catelogIP.toString() + "/reloadItem/");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("itemNumber", ""+itemNumber));
        urlParameters.add(new BasicNameValuePair("reloadAmount", ""+reloadAmount));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            reqResult = EntityUtils.toString(response.getEntity());

            if( reqResult.contains("true") ){
                displayResult = "reload item " + itemNumber + " " + reloadAmount + " more success";
            } else{
                displayResult = "reload item " + itemNumber + " " + reloadAmount + " more fail";
            }
            System.out.println(displayResult);
            writeIntoFile(displayResult);
        } catch (Exception e){
            displayResult = "reload item " + itemNumber + " " + reloadAmount + " more fail";
            System.out.println(displayResult);
            writeIntoFile(displayResult);
        }
    }

    private void writeIntoFile(String s) throws Exception{
        CSVWriter writer = new CSVWriter(new FileWriter(orderHistoryURL, true));
        String [] record = s.split(",");
        writer.writeNext(record);
        writer.close();
    }

    public OrderServer withSelfIP(String addr){
        ip = new IP().withString(addr);
        return this;
    }

    public OrderServer withFrontEndIP(String addr){
        frontEndIP = new IP().withString(addr);
        return this;
    }

    public OrderServer withCatelogIP(String addr){
        catelogIP = new IP().withString(addr);
        return this;
    }

    public OrderServer withOrderHistoryURL(String url) throws Exception {
        orderHistoryURL = url;
        File tempFile = new File(orderHistoryURL);
        tempFile.delete();
        File newFile = new File(orderHistoryURL);
        return this;
    }


}