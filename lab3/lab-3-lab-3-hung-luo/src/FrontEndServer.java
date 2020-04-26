package com.dslab2;
import java.io.FileWriter;
import java.io.File;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class FrontEndServer {

    public com.dslab2.IP ip;
    private com.dslab2.IP catelogIP;
    private com.dslab2.IP orderIP;

    private String logFileURL;

    private Cache cache = new Cache();

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
            // Check cache
            if(cache.check(itemNumber)) {
                reqResult = cache.get(itemNumber);
                if (reqResult.equals("Book Not Found") == false) {
                    displayResult = "lookUp cache item number " + itemNumber + " success";
                } else {
                    displayResult = "lookUp cache item number " + itemNumber + " fail";
                }
                System.out.println(displayResult);
                writeIntoLogFile(displayResult);
                return reqResult;
            } else {
                System.out.println("http://" + catelogIP.toString() + "/queryItem/" + itemNumber);
                HttpGet request = new HttpGet("http://" + catelogIP.toString() + "/queryItem/" + itemNumber);
                CloseableHttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    reqResult = EntityUtils.toString(entity);
                    cache.put(itemNumber, reqResult);
                }
            }
            if (reqResult.equals("Book Not Found") == false) {
                displayResult = "lookUp catalog server item number " + itemNumber + " success";
            } else {
                displayResult = "lookUp catalog server item number " + itemNumber + " fail";
            }
            System.out.println(displayResult);
            writeIntoLogFile(displayResult);
            return reqResult;
        } catch (Exception e) {
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

    public Boolean invalidate(int itemNumber) throws Exception {
        Boolean result = false;

        if( cache.check(itemNumber) ) {
            // remove item from cache
            cache.remove(itemNumber);
            result = true;
        }

        return result;
    }

    public FrontEndServer withSelfIP(String addr){
        ip = new com.dslab2.IP().withString(addr);
        return this;
    }

    public FrontEndServer withCatelogIP(String addr){
        catelogIP = new com.dslab2.IP().withString(addr);
        return this;
    }

    public FrontEndServer withOrderIP(String addr){
        orderIP = new com.dslab2.IP().withString(addr);
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