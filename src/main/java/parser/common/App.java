package parser.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



import javax.net.ssl.HttpsURLConnection;

import parser.common.dao.DrugLinkDAO;
import parser.common.model.DrugLink;



public class App {

    private static final Logger logger = LogManager.getLogger(App.class);
	private static final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {

        logger.info("Initializing");

        ApplicationContext appContext = new ClassPathXmlApplicationContext("Spring-Module.xml");
        DrugLinkDAO drugLinkDAO = (DrugLinkDAO) appContext.getBean("drugLinkDAO");
        JSONParser parser = new JSONParser();
        int len = 100;
for (int i=1;i<1000000;i++){

        ArrayList<String> test = drugLinkDAO.getRxcui(i*len,len);

        for(String rxcui : test){
            logger.info(rxcui);

            String r = sendGet(rxcui);
            String title = "";
            String href = "";
            String rel = "";
            String updated = "";

            try{
            	Object obj = parser.parse(r);

                JSONObject jo = ((JSONObject)obj);
                jo = (JSONObject)(jo.get("feed"));
                
                JSONArray array = (JSONArray)(jo.get("entry"));

                jo = (JSONObject)(array.get(0));

				updated = ((JSONObject)(jo.get("updated"))).get("_value").toString();

                array = (JSONArray)(jo.get("link"));
                jo = (JSONObject)(array.get(0));

                title = (jo.get("title")).toString();
                href = (jo.get("href")).toString();
                rel = (jo.get("rel")).toString();

                logger.info(updated);
                logger.info(title);
                logger.info(href);
                logger.info(rel);
                DrugLink dl = new DrugLink();
                dl.setRxcui(rxcui);
                dl.setTitle(title);
                dl.setLink(href);
                dl.setRel(rel);
                dl.setUpdated(updated);
                drugLinkDAO.insert(dl);
                Thread.sleep(1000);

           }catch(Exception e){
            	logger.info(e.getMessage());
            	Thread.sleep(1000);
            	try{
            	DrugLink dl = new DrugLink();
                dl.setRxcui(rxcui);
                dl.setTitle(title);
                dl.setLink(href);
                dl.setRel(rel);
                dl.setUpdated(updated);
                drugLinkDAO.insert(dl);
                }catch(Exception ex){
                	logger.info(ex.getMessage());
                }
           }
        }
}
//        sendGet();


    }

	private static String sendGet(String rxcui) throws Exception {

		String url = "https://apps.nlm.nih.gov/medlineplus/services/mpconnect_service.cfm?knowledgeResponseType=application/json&mainSearchCriteria.v.cs=2.16.840.1.113883.6.88&mainSearchCriteria.v.c="+rxcui;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();

		//print result
		//System.out.println(response.toString());

	}

}

