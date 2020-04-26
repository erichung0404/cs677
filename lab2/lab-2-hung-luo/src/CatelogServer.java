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

public class CatelogServer {
    public IP ip;
    private IP frontEndIP;
    private IP orderIP;

    private String dataURL;
    private String logFileURL;

    private List<Book> bookList;

    public HashMap<Integer, String> querySubject(String subject) throws Exception{
        HashMap<Integer, String> items = new HashMap<>();

        loadCSV();
        ListIterator<Book> list_Iter = bookList.listIterator(0);
        while(list_Iter.hasNext()){
            Book tempBook = list_Iter.next();
            if (tempBook.topic.equals(subject)){
                items.put( tempBook.itemNumber, tempBook.name );
            }
        }
        closeCSV();

        if(items.size() > 0){
            System.out.println("query subject " + subject + " success");
            writeIntoLogFile("query subject " + subject + " success");
        } else{
            System.out.println("query subject " + subject + " failed");
            writeIntoLogFile("query subject " + subject + " failed");
        }
        return items;
    }

    public String queryItem(int itemNumber) throws Exception{
        loadCSV();
        ListIterator<Book> list_Iter = bookList.listIterator(0);
        Book tempBook = new Book(100,"100","100",100,100);
        while(list_Iter.hasNext()){
            tempBook = list_Iter.next();
            if (tempBook.itemNumber == itemNumber ){
                break;
            }
        }

        closeCSV();

        if ( tempBook.itemNumber == itemNumber ){
            System.out.println("query item " + itemNumber + " success");
            writeIntoLogFile("query item " + itemNumber + " success");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson( tempBook );
        } else{
            System.out.println("query item " + itemNumber + " failed");
            writeIntoLogFile("query item " + itemNumber + " fail");
            return "Book Not Found";
        }
    }


    public Boolean requestItem(int itemNumber) throws Exception{
        Boolean result = false;
        Book tempBook = new Book(100,"100","100",100,100);
        loadCSV();
        ListIterator<Book> list_Iter = bookList.listIterator(0);
        while(list_Iter.hasNext()){
            tempBook = list_Iter.next();
            if ( tempBook.itemNumber == itemNumber && tempBook.stack > 0 ){
                result = true;
                tempBook.stack -= 1;
                break;
            }
        }
        closeCSV();

        if ( result == true ){
            System.out.println("request item " + itemNumber + " success, " + tempBook.stack + " left");
            writeIntoLogFile("request item " + itemNumber + " success, " + tempBook.stack + " left");
        } else{
            System.out.println("request item " + itemNumber + " fail, " + tempBook.stack + " left");
            writeIntoLogFile("request item " + itemNumber + " fail, " + tempBook.stack + " left");
        }

        return result;
    }

    public Boolean reloadItem(int itemNumber, int reloadAmount) throws Exception {
        Boolean result = false;
        Book tempBook = new Book(100,"100","100",100,100);
        loadCSV();
        ListIterator<Book> list_Iter = bookList.listIterator(0);
        while(list_Iter.hasNext()){
            tempBook = list_Iter.next();
            if ( tempBook.itemNumber == itemNumber ){
                result = true;
                tempBook.stack += reloadAmount;
                break;
            }
        }
        closeCSV();

        if ( result == true ){
            System.out.println("reload item " + itemNumber + " " + reloadAmount + " more, " + tempBook.stack + " left");
            writeIntoLogFile("reload item " + itemNumber + " " + reloadAmount + " more, " + tempBook.stack + " left");
        } else{
            System.out.println("reload item " + itemNumber + " " + reloadAmount + " fail");
            writeIntoLogFile("reload item " + itemNumber + " " + reloadAmount + " fail");
        }

        return result;
    }

    public Boolean updateCost(int itemNumber, int cost) throws Exception{
        Boolean result = false;
        loadCSV();
        ListIterator<Book> list_Iter = bookList.listIterator(0);
        while(list_Iter.hasNext()){
            Book tempBook = list_Iter.next();
            if ( tempBook.itemNumber == itemNumber ){
                result = true;
                tempBook.cost = cost;
                break;
            }
        }
        closeCSV();

        if ( result == true ){
            System.out.println("update item " + itemNumber + " cost to " + cost + " success");
            writeIntoLogFile("update item " + itemNumber + " cost to " + cost + " success");
        } else{
            System.out.println("update item " + itemNumber + " cost to " + cost + " fail");
            writeIntoLogFile("update item " + itemNumber + " cost to " + cost + " fail");
        }

        return result;
    }

    public void loadCSV() throws Exception {
        bookList = new LinkedList<Book>();
        CSVReader reader = new CSVReader(new FileReader(dataURL),',','"',0);
        String[] nextLine;

        while( (nextLine = reader.readNext())!= null ){
            if (nextLine != null){
                bookList.add(new Book( Integer.parseInt(nextLine[0]), nextLine[1], nextLine[2], Integer.parseInt(nextLine[3]), Integer.parseInt(nextLine[4])  ));
            }
        }

        reader.close();
    }

    public void closeCSV() throws Exception{
        CSVWriter writer = new CSVWriter(new FileWriter(dataURL));

        ListIterator<Book> list_Iter = bookList.listIterator(0);
        while(list_Iter.hasNext()){
            Book thisBook = list_Iter.next();
            String [] record = thisBook.toString().split(",");
            writer.writeNext(record);
        }

        writer.close();
    }

    public CatelogServer withSelfIP(String addr){
        ip = new IP().withString(addr);
        return this;
    }

    public CatelogServer withFrontEndIP(String addr){
        frontEndIP = new IP().withString(addr);
        return this;
    }

    public CatelogServer withOrderIP(String addr){
        orderIP = new IP().withString(addr);
        return this;
    }

    public CatelogServer withDataURL(String url){
        dataURL = url;
        return this;
    }

    public CatelogServer withLogFileURL(String url) throws Exception{
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