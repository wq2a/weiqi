package parser.common.config.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.ArrayList;

import org.springframework.batch.item.ItemProcessor;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import parser.common.model.KeywordDTO;
import parser.common.model.WikiLinkDTO;

public class WikiLinkItemProcessor implements ItemProcessor<KeywordDTO, ArrayList<WikiLinkDTO>> {

    private static final Logger logger = LogManager.getLogger(WikiLinkItemProcessor.class);

    private final RestTemplate restTemplate;

    public WikiLinkItemProcessor() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ArrayList<WikiLinkDTO> process(final KeywordDTO str) throws Exception {

        logger.info(str);

        String url = buildURL(str);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Thread.sleep(1000);

        return parser(str, response);

    }

    /**
     * https://en.wikipedia.org/w/api.php?action=query&prop=info&titles=Main%20page&redirects&inprop=url
     */
    private String buildURL(final KeywordDTO str) {
        StringBuffer url = new StringBuffer("https://en.wikipedia.org/w/api.php?action=query&prop=info&redirects&inprop=url");
        url.append("&titles=").append(str.getStr());
        return url.toString();
    }

    private ArrayList<WikiLinkDTO> parser(final KeywordDTO str, ResponseEntity<String> response) {

        ArrayList<WikiLinkDTO> wikilinks = new ArrayList<WikiLinkDTO>();

        if(response.getStatusCode() != HttpStatus.OK) {
            WikiLinkDTO wikilink = new WikiLinkDTO();
            wikilink.setStr(str.getStr());
            wikilink.setSab(str.getSab());
            wikilinks.add(wikilink);
            return wikilinks;
        }
        
        String r = response.getBody().toString();

        JSONParser parser = new JSONParser();
        String title = "";
        String href = "";
        String pageid = "";
        String touched = "";

        try {
            Object obj = parser.parse(r);
            JSONObject jo = ((JSONObject)obj);

            jo = (JSONObject)(jo.get("query"));
            jo = (JSONObject)(jo.get("pages"));
                    
            Iterator<String> keys = jo.keySet().iterator();
            while(keys.hasNext()) {
                String keyValue = (String)keys.next();
                JSONObject temp = (JSONObject)(jo.get(keyValue));

                WikiLinkDTO wikilink = new WikiLinkDTO();
                wikilink.setStr(str.getStr());
                wikilink.setSab(str.getSab());

                title = (temp.get("title")).toString();
                href = (temp.get("fullurl")).toString();
                pageid = (temp.get("pageid")).toString();
                touched = (temp.get("touched")).toString();
            
                wikilink.setTitle(title);
                wikilink.setLink(href);
                wikilink.setPageid(pageid);
                wikilink.setUpdated(touched);
                wikilinks.add(wikilink);
            }

            for(WikiLinkDTO wiki : wikilinks)
                logger.info(wiki);

        } catch (Exception e) {
            logger.error(e.getMessage());
            WikiLinkDTO wikilink = new WikiLinkDTO();
            wikilink.setStr(str.getStr());
            wikilink.setSab(str.getSab());
            wikilinks.add(wikilink);
 
            return wikilinks;
        }

        if(wikilinks.size() == 0){
            WikiLinkDTO wikilink = new WikiLinkDTO();
            wikilink.setStr(str.getStr());
            wikilink.setSab(str.getSab());
            wikilinks.add(wikilink);

        }

        return wikilinks;
    }

}
