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

public class FrontEndServer {

    public IP ip;
    private IP catelogIP;
    private IP orderIP;

    private String logFileURL;

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public void FrontEndServer(){

    }

    public String search(String topic) throws Exception{
        // Quick Check
        if ( topic.equals("distributed systems") == false && topic.equals("graduate school") == false ){
            return "No topic exists, please enter distributed systems or graduate school";
        }

        String reqResult ="";
        String displayResult = "";
        try {
            String reqURL = "http://" + catelogIP.toString() + "/querySubject/"+topic;
            reqURL = reqURL.replace(" ", "%20");
            HttpGet request = new HttpGet(reqURL);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                reqResult = EntityUtils.toString(entity);
            }

            if( reqResult.equals("") == false ){
                displayResult = "search " + topic + " success";
            } else{
                displayResult = "search " + topic + " fail";
            }
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        } catch (Exception e){
            displayResult = "search " + topic + " fail";
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        }
    }

    public String lookUp(int itemNumber) throws Exception{
        // Quick Check
        if ( itemNumber < 0 || itemNumber > 3 ){
            return "No item exists, please enter number between 0~3";
        }

        String reqResult ="";
        String displayResult = "";
        try {
            System.out.println("http://" + catelogIP.toString() + "/queryItem/"+itemNumber);
            HttpGet request = new HttpGet("http://" + catelogIP.toString() + "/queryItem/"+itemNumber);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                reqResult = EntityUtils.toString(entity);
            }

            if( reqResult.equals("Book Not Found") == false ){
                displayResult = "lookUp " + itemNumber + " success";
            } else{
                displayResult = "lookUp " + itemNumber + " fail";
            }
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        } catch (Exception e){
            displayResult = "lookUp " + itemNumber + " fail";
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        }
    }

    public String buy(int itemNumber) throws Exception{
        // Quick Check
        if ( itemNumber < 0 || itemNumber > 3 ){
            return "No item exists, please enter number between 0~3";
        }

        String reqResult ="";
        String displayResult = "";
        try {
            HttpGet request = new HttpGet("http://" + orderIP.toString() + "/buy/"+itemNumber);
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
            writeIntoLogFile(displayResult);
            return reqResult;
        } catch (Exception e){
            displayResult = "buy " + itemNumber +" fail";
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        }
    }

    public FrontEndServer withSelfIP(String addr){
        ip = new IP().withString(addr);
        return this;
    }

    public FrontEndServer withCatelogIP(String addr){
        catelogIP = new IP().withString(addr);
        return this;
    }

    public FrontEndServer withOrderIP(String addr){
        orderIP = new IP().withString(addr);
        return this;
    }

    public FrontEndServer withLogFileURL(String url) throws Exception{
        logFileURL = url;
        File tempFile = new File(url);
        tempFile.delete();
        tempFile.createNewFile();
        return this;
    }

    private void writeIntoLogFile(String s) throws Exception{
        CSVWriter writer = new CSVWriter(new FileWriter(logFileURL, true));
        String [] record = s.split(",");
        writer.writeNext(record);
        writer.close();
    }

}