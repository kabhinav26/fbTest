package facebookTest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.*;
import java.io.*;
import java.lang.ProcessBuilder;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kumar on 21/07/17.
 */

public class FacebookFeedVerification {

    private static final Logger logger= LoggerFactory.getLogger(FacebookFeedVerification.class);
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String SOLR_URL="http://seoulsolr6-01.uata.lokal:8983/solr/productCollectionNew/";
    private static final String SCRIPT_PATH="/home/jenkins/ShellScripts/";
    private static final String APIURL="http://xsearchapp-01.uata.lokal:8080/search-service/api/search/feed/product/fb?storeId=10001&channelId=web&clientId=1&requestId=1&username=a";
    private static final String UNSYNC_PROD="TOA-15629-00090,Tes Unsync Single - 2,tes unsync single - 2,new,http://www.blibli.com/tes-unsync-single-2-TOA.15629.00090.html,http://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/acer_tes-unsync-single---2_full08.jpg,in stock,11111,11111,Acer,blibli://product/TOA-15629-00090,blibli://product/TOA-15629-00090,Fashion Pria,Baju Atasan Formal,Formal Lengan Panjang,Tes Unsync Single - 2,1034231507,Blibli App - iOS,blibli.mobile.commerce,Blibli App - Android";
    private static final String SYNC_PROD="MTA-0308935,Prod Facebook delete verification,prod facebook delete verification,new,http://www.blibli.com/prod-facebook-delete-verification-MTA.0308935.htm,http://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium//93/MTA-0308935/lg_prod-facebook-delete-verification_full01.jpg,in stock,2089179,2321310,LG,blibli://product/MTA-0308935,blibli://product/MTA-0308935,Handphone & Tablet,Handphone,Android,Prod Facebook delete verification,1034231507,Blibli App - iOS,blibli.mobile.commerce,Blibli App - Android";


    public  static void main(String args[]) throws Exception
    {
        int responseCode=runFullFacebookFeed();

        if(responseCode==200) {

            logger.info("----------------------------------------------------------");
            logger.info("Starting FB Full feed Test");
            logger.info("Ran facebook full feed successfully. Now executing Test cases");

            long count = getSolrProdCount();
            int count1 = getCountofWrittenRecords();
            if(count == count1)
                logger.info("Test case1: Match Count of SOLR and Facebook Full Feed. Passed.  SolrCount:{} FeedCount:{}.",count,count1);
            else
                logger.info("Test case1: Match Count of SOLR and Facebook Full Feed. Failed.  SolrCount:{} FeedCount:{}.",count,count1);
            if(getFeedExclusionResult() == 1)
                logger.info("Test case2: Feed Exclusion products. Passed");
            else
                logger.info("Test case2: Feed Exclusion products. Failed");

            checkDetailsForProd();
        }
        else
            logger.info("Running Facebook feed did not return 200 response. Rsponse code received:{}",responseCode);
    }

    public static long getSolrProdCount() throws Exception{
        HttpSolrClient http=new HttpSolrClient(SOLR_URL);
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.addFilterQuery("published:1 AND isInStock:1 AND buyable:1");
        solrQuery.addFilterQuery("{!collapse field=level1Id sort='merchantScore desc'}");
        solrQuery.addFilterQuery("-nameSearch:(\"samsung\" & \"note 7\")");
        solrQuery.addFilterQuery("-salesCatalogCategoryIds:(54826 54923 54921)");
        solrQuery.addFilterQuery("-productCode:(MTA-0307497 MTA-0307496)");
        solrQuery.setRows(1);
        solrQuery.setFields("level1Id");
        QueryResponse queryResponse = http.query(solrQuery);
        return queryResponse.getResults().getNumFound();
    }

    public static int getCountofWrittenRecords() throws Exception{
        int countofFiles=getShellScriptOutput("countFacebookFile.sh");
        int countofRecords=getShellScriptOutput("countFacebookFileRecords.sh");
        int diff=(countofRecords-countofFiles)+1;
        return diff;
    }

    public static int runFullFacebookFeed() throws Exception {
        URL url=new URL(APIURL);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        return responseCode;
    }

    public static int getShellScriptOutput(String Filename) throws Exception{
        ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+Filename);
        //       processBuilder.inheritIO();
        Process process=processBuilder.start();
        int exitValue=process.waitFor();
        String line;
        if (exitValue != 0) {
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("execution of script failed!");
        }
        else {
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            if((line = reader.readLine())!= null && org.apache.commons.lang3.StringUtils.isNumeric(line))
                return Integer.parseInt(line);
            else
                return Integer.parseInt(reader.readLine());

        }
    }

    public static String getShellScriptActualOutput(String Filename,String prodType) throws Exception{
        ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+Filename,prodType);
        Process process=processBuilder.start();
        int exitValue=process.waitFor();
        String line;
        if (exitValue != 0) {
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("Unable to get data from File!");
        }
        else {
            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            if((line = reader.readLine())!= null)
                return line;
        }
        return "abcd";
    }


    public static int getFeedExclusionResult() throws Exception{
        return getShellScriptOutput("verifyFeedExclusion.sh");
    }

    public static void checkDetailsForProd() throws Exception{

        String retUnsyncProd=getShellScriptActualOutput("verifyFacebookRecords.sh","TOA-15629-00090");
        String retSyncProd=getShellScriptActualOutput("verifyFacebookRecords.sh","MTA-0308935");

        if(retSyncProd.equals(SYNC_PROD) && retUnsyncProd.equals(UNSYNC_PROD))
        {
            logger.info("Test case3: Expected sync(MTA-0308935) and unsync(TOA-15629-00090) products are present and Matches. Passed");
            logger.info("----------------------------------------------------------");
        }
        else
        {
            logger.info("Test case3: Expected sync(MTA-0308935) and unsync(TOA-15629-00090) products are either absent or does not match. Failed");
            logger.info("----------------------------------------------------------");
        }

    }
}
