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

public class Client {

    private IP frontEndIP;
    private String testFileURL;

    private String logFileURL;

    private List<String> testCommandList = new LinkedList<>();

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private double avgLookUpDuration = 0;

    public Client(){
        // new repeatRequestThread().start();
        new runTestCommandThread().start();
    }

    public class repeatRequestThread extends Thread {
        public void run()  {
            try{
                Thread.sleep(2000);
            } catch (Exception e){

            }

            while(true){
                String completeCommand  = "http://" + frontEndIP.toString() + "/buy/0";
                try {
                    Thread.sleep(500);
                    runRepeatGetCommand(completeCommand);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void runRepeatGetCommand(String reqURL) throws Exception{
        String reqResult ="";
        String displayResult = "";
        try {
            reqURL = reqURL.replace(" ", "%20");
            HttpGet request = new HttpGet(reqURL);

            long startTime = System.currentTimeMillis();

            CloseableHttpResponse response = httpClient.execute(request);

            long endTime = System.currentTimeMillis();
            long LookUpDuration = endTime - startTime;
            avgLookUpDuration = (double) ((avgLookUpDuration * 29 + LookUpDuration)/30);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                reqResult = EntityUtils.toString(entity);
            }
            System.out.println( "Average Time:" + avgLookUpDuration);
            System.out.println( "Run command:" + reqURL);
            System.out.println( "Result:" + reqResult);
        } catch (Exception e){
            System.out.println( "Run command:" + reqResult);
            System.out.println( "Result:" + reqResult);
        }
    }

    public class runTestCommandThread extends Thread{
        public void run() {
            while(true){
                String completeCommand;
                try {
                    Thread.sleep(2000);
                    ListIterator<String> list_Iter = testCommandList.listIterator(0);
                    while(list_Iter.hasNext()){
                        String command = list_Iter.next();
                        completeCommand = "http://" + frontEndIP.toString() + command;
                        System.out.println("command:" + completeCommand);
                        runGetCommand(completeCommand);
                        Thread.sleep(2000);
                    }
                    System.out.println("Command List Tested");
                    writeIntoLogFile("Command List Tested");
                    break;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void runGetCommand(String reqURL) throws Exception{
        String reqResult ="";
        String displayResult = "";
        try {
            reqURL = reqURL.replace(" ", "%20");
            HttpGet request = new HttpGet(reqURL);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                reqResult = EntityUtils.toString(entity);
            }
            System.out.println( "Run command:" + reqURL);
            System.out.println( "Result:" + reqResult);
            writeIntoLogFile( "Run command:" + reqURL);
            writeIntoLogFile( "Result:" + reqResult);
        } catch (Exception e){
            System.out.println( "Run command:" + reqResult);
            System.out.println( "Result:" + reqResult);
            writeIntoLogFile( "Run command:" + reqResult);
            writeIntoLogFile( "Result:" + reqResult);
        }
    }

    public void loadTestFile() throws Exception {
        CSVReader reader = new CSVReader(new FileReader(testFileURL),',','"',0);
        String[] nextLine;

        while( (nextLine = reader.readNext())!= null ){
            if (nextLine != null){
                testCommandList.add(nextLine[0]);
            }
        }
        reader.close();
    }

    public Client withFrontEndIP(String addr){
        frontEndIP = new IP().withString(addr);
        return this;
    }

    public Client withTestFileURL(String url) throws Exception{
        testFileURL = url;
        loadTestFile();
        return this;
    }

    public Client withLogFileURL(String url) throws Exception{
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

    public static void main(String[] args) throws Exception{
        // Check if enter all ip address
        if(args.length < 2){
            System.out.println("Please enter FrontEnd IPaddrss:port, test request file");
            return;
        }

        new Client().withFrontEndIP(args[0]).withTestFileURL(args[1]).withLogFileURL(args[2]);

        System.out.println("Client Created");
    }

}
