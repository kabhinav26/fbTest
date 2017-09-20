package facebookTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


/**
 * Created by kumar on 26/07/17.
 */
public class FacebookExclusionVerification {

    private static final Logger logger= LoggerFactory.getLogger(FacebookExclusionVerification.class);
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String APIURL="http://xsearchapp-01.uata.lokal:8080/search-service/api/search/feed-exclusion-entity/";
    private static final String APIURL1="http://xsearchapp-01.uata.lokal:8080/search-service/api/search/feed/product/";
    private static final String SCRIPT_PATH="/home/jenkins/ShellScripts/";
    private static final String CONFIGURL="http://xsearchapp-01.uata.lokal:8080/search-service/api/search/config/";

    public static void  main(String args[]) throws Exception{

        logger.info("----------------------------------------------------------");
        logger.info("Starting Test 1: Check if feed is run after exclusion change");
        String oldTimestamp=getTimeStampOfExistingFiles();
        insertExclusion();
        deleteExclusion();
        String newTimeStamp=getTimeStampOfExistingFiles();
        checkFullFeedRan(oldTimestamp,newTimeStamp,"exclusion is changed");
        TimeUnit.MINUTES.sleep(1);
        logger.info("----------------------------------------------------------");
        logger.info("Starting Test2 : Check if feed is run after  prod count is less than x% set in cutoff");
        checkCutOffFbFeed();
        logger.info("----------------------------------------------------------");
        logger.info("Starting Test3 : Check if cut off set to x% after feed run");
        checkCutoffValueSet();
    }

    public static String getExclusionPresent() throws Exception {
        URL url=new URL(APIURL+"find?storeId=10001&channelId=web&clientId=789&requestId=798798&username=abc&word=testqa&page=0&size=1");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode==200){
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            ObjectMapper mapper=new ObjectMapper();
            Example example = mapper.readValue(body, Example.class);
            logger.info("Finding the added exclusion:{}",body);
            return  example.getContent().get(0).getId();
        }
        else
            logger.info("Response Code from getExclusionPresent is not 200");
            return null;
    }

    public static void insertExclusion() throws Exception{
        URL url=new URL(APIURL+"save?storeId=10001&channelId=web&clientId=789&requestId=798798&username=abc");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id","");
        jsonObject.put("key","nameSearch");
        jsonObject.put("value","testqa");
        jsonObject.put("feedType","FACEBOOK");
        jsonObject.put("positiveFilter","false");
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(con.getOutputStream());
        outputStreamWriter.write(jsonObject.toString());
        outputStreamWriter.close();
        int responseCode = con.getResponseCode();
        if (responseCode==200){
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            logger.info("Response after adding exclusion:{}",body);
        }
        else
            logger.info("Response Code from insertExclusion() is not 200");
    }

    public static void deleteExclusion() throws Exception{
        URL url=new URL(APIURL+"delete?storeId=10001&channelId=web&clientId=789&requestId=798798&username=abc");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        String idStr=getExclusionPresent();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",idStr);
        jsonObject.put("key","nameSearch");
        jsonObject.put("value","testqa");
        jsonObject.put("feedType","FACEBOOK");
        jsonObject.put("positiveFilter","false");
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(con.getOutputStream());
        outputStreamWriter.write(jsonObject.toString());
        outputStreamWriter.close();
        int responseCode = con.getResponseCode();
        if (responseCode==200){
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            logger.info("Response after adding exclusion:{}",body);
            runExclusionChangeApi();
        }
        else
            logger.info("Response Code from insertExclusion() is not 200");
    }

    public static void runExclusionChangeApi() throws Exception{
        URL url=new URL(APIURL1+"exclusion/change?storeId=10001&channelId=web&clientId=789&requestId=798798&username=abc&destination=facebook");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode=con.getResponseCode();
        if(responseCode==200){
            logger.info("Successfully ran runExclusionChangeApi.");
        }
        else
        {
            logger.info("Failed to run runExclusionChangeApi. Response Code:{}",responseCode);
        }
    }

    public static String getTimeStampOfExistingFiles() throws Exception{
        ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+"getTimeStamp.sh");
        Process process=processBuilder.start();
        int exitValue=process.waitFor();
        String line;
        if (exitValue != 0) {
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("Unable to get data from File!");
        }
        else {
            StringBuffer uniqTime=new StringBuffer();
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((line = reader.readLine())!= null)
            {
                uniqTime.append(line);
                uniqTime.append(",");
            }
                return uniqTime.toString();
        }
    }

    public static void checkFullFeedRan(String oldT,String newT,String testName) throws Exception{
        if(!oldT.equals(newT)){
            logger.info("Successfully compared timestamp. Feed is run after exclusion change.");
            logger.info("TimeStamp of Older files:{} and TimeStamp of Newer files:{}",oldT,newT);
            logger.info("Passed Test: Check if feed is run after {}",testName);
            logger.info("----------------------------------------------------------");
        }
        else
        {
            logger.info("Successfully compared timestamp. Feed is not run after exclusion change.");
            logger.info("TimeStamp of Older files:{} and TimeStamp of Newer files:{}",oldT,newT);
            logger.info("Passed Test: Check if feed is run after {}",testName);
            logger.info("----------------------------------------------------------");
        }
    }

    public static void checkCutOffFbFeed() throws Exception{
        setConfigCutOff();
        String oldTimestamp=getTimeStampOfExistingFiles();
        URL url=new URL(APIURL1+"cut-off/limit?storeId=10001&channelId=web&clientId=789&requestId=798798&username=abc&destination=facebook");
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode=con.getResponseCode();
        if(responseCode==200){
            logger.info("Successfully ran checkCutOffFbFeed.");
            String newTimeStamp=getTimeStampOfExistingFiles();
            checkFullFeedRan(oldTimestamp,newTimeStamp,"Cut-off is breached");
        }
        else
        {
            logger.info("Failed to run checkCutOffFbFeed. Response Code:{}",responseCode);
        }
    }

    public static String getConfigId(String type,String configName) throws Exception{
        URL url=new URL(CONFIGURL+"find-by-name?storeId=10001&channelId=web&clientId=1&requestId=1&username=1&name="+configName);
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode=connection.getResponseCode();
        if(responseCode==200){
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            logger.info("Successfully found config {} :{}",configName,body);
            ObjectMapper mapper=new ObjectMapper();
            Config config=mapper.readValue(body,Config.class);
            if(type.equals("id"))
                return config.getValue().getId();
            else if(type.equals("value"))
                return config.getValue().getValue();
            else
                return config.getValue().getLabel();
        }
        else
        {
            logger.info("Failed to run getConfigId. Response Code:{}",responseCode);
            return null;
        }
    }

    public static void setConfigCutOff() throws Exception{
        URL url=new URL(CONFIGURL+"update?storeId=10001&channelId=web&clientId=1&requestId=1&username=1");
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        String idStr=getConfigId("id","facebook.feed.size.cut.off");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",idStr);
        jsonObject.put("name","facebook.feed.size.cut.off");
        jsonObject.put("label","facebook.feed.size.cut.off");
        jsonObject.put("value","20000");
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(jsonObject.toString());
        outputStreamWriter.close();
        int responseCode = connection.getResponseCode();
        if (responseCode==200){
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String body = IOUtils.toString(in, encoding);
            logger.info("Response After setting config:{}",body);
        }
        else
            logger.info("Failed to run setConfigCutOff() Response Code:{}",responseCode);
    }

    public static void checkCutoffValueSet() throws Exception{
            long solrCount=FacebookFeedVerification.getSolrProdCount();
            int cut= Integer.parseInt(getConfigId("value","facebook.cut.off.percentage"));
            double cutOff=solrCount*cut/100;
            double cutConfig=Double.parseDouble(getConfigId("value","facebook.feed.size.cut.off"));
            if(cutConfig==cutOff) {
                logger.info("Value is set to {} of count. Total:{}, calculated:{}, set:{}",cut, solrCount, cutOff, cutConfig);
                logger.info("Passed Test: Check if cut off set to x% after feed run");
                logger.info("----------------------------------------------------------");
            }
            else {
                logger.info("Value is not set to {} of count. Total:{}, calculated:{}, set:{}", cut, solrCount, cutOff, cutConfig);
                logger.info("Failed Test: Check if cut off set to x% after feed run");
                logger.info("----------------------------------------------------------");
            }
    }
}
