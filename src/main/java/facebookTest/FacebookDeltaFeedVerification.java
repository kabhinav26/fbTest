package facebookTest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by kumar on 31/07/17.
 */

public class FacebookDeltaFeedVerification {

    private static final Logger logger= LoggerFactory.getLogger(FacebookDeltaFeedVerification.class);
    private static final String SOLR_URL="http://seoulsolr6-01.uata.lokal:8983/solr/productCollectionNew/";
    private static final String APIURL="http://xsearchapp-01.uata.lokal:8080/search-service/api/search/feed/product/fb/delta?storeId=10001&channelId=web&clientId=1&requestId=1&username=1";
    private static final String USER_AGENT="Mozilla/5.0";
    private static final String SCRIPT_PATH="/home/jenkins/ShellScripts/";

    public static void main(String args[]) throws Exception{

        logger.info("----------------------------------------------------------");
        logger.info("Starting FB Delta feed Test");

        if(runDelta()==200){
            logger.info("Ran facebook FB delta feed successfully. Now executing Test cases");
            if(getSolrProdCount()==checkCountInFile())
                logger.info("Test Case 1: Passed - Count from SOLR {} and Count in File {} is matching",getSolrProdCount(),checkCountInFile());
            else
                logger.info("Test Case 1: Failed - Count from SOLR {} and Count in File {} is not matching",getSolrProdCount(),checkCountInFile());
        }

    }

    public static int runDelta() throws Exception{
        URL url=new URL(APIURL);
        HttpURLConnection con=(HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        return responseCode;
    }

    public static long getSolrProdCount() throws Exception{
        HttpSolrClient http=new HttpSolrClient(SOLR_URL);
        String startTime = FacebookExclusionVerification.getConfigId("value","facebook.feed.last.updated.date");
        Date end=new Date();
        String endTime=Long.toString(end.getTime());
        String queryText = "xProductLastUpdatedTimestamp:["  + startTime  + " TO " + endTime + "] OR xinventryServiceLastUpdatedTimestamp:["  + startTime  + " TO " + endTime + "]" ;
        //System.out.println("queryText:"+queryText);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(queryText);
        solrQuery.addFilterQuery("{!collapse field=level1Id sort='merchantScore desc'}");
        solrQuery.addFilterQuery("-nameSearch:(\"samsung\" & \"note 7\")");
        solrQuery.addFilterQuery("-salesCatalogCategoryIds:(54826 54923 54921)");
        solrQuery.addFilterQuery("-productCode:(MTA-0307497 MTA-0307496)");
        solrQuery.setRows(1);
        solrQuery.setFields("level1Id");
        QueryResponse queryResponse = http.query(solrQuery);
        return queryResponse.getResults().getNumFound();
    }

    public static int getShellScriptOutput(String Filename) throws Exception{
        ProcessBuilder processBuilder=new ProcessBuilder(SCRIPT_PATH+Filename);
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

    public static int checkCountInFile() throws Exception{
        int countofFiles=getShellScriptOutput("countFacebookDeltaFiles.sh");
        int countofRecords=getShellScriptOutput("countFacebookDeltaRecords.sh");
        int diff=(countofRecords-countofFiles)+1;
        return diff;
    }
}
