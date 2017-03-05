package parser.common.config.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import parser.common.model.CodeDTO;
import parser.common.model.MPLinkDTO;

public class MPLinkItemProcessor implements ItemProcessor<CodeDTO, ArrayList<MPLinkDTO>> {

    private static final Logger logger = LogManager.getLogger(MPLinkItemProcessor.class);

    private final RestTemplate restTemplate;

    public MPLinkItemProcessor() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ArrayList<MPLinkDTO> process(final CodeDTO code) throws Exception {

        logger.info(code);

        String url = buildURL(code);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Thread.sleep(1000);

        return parser(code, response);

    }

    /**
     * https://medlineplus.gov/connect/service.html
     */
    private String buildURL(final CodeDTO code) {
        StringBuffer url = new StringBuffer("https://apps.nlm.nih.gov/medlineplus/services/mpconnect_service.cfm?knowledgeResponseType=application/json");
        url.append("&mainSearchCriteria.v.cs=");

        String sab = code.getSab();
        String c = code.getCode();

        if (sab.equals("ICD9CM")) {
            url.append("2.16.840.1.113883.6.103");
        }else if(sab.equals("ICD10CM")){
            url.append("2.16.840.1.113883.6.90");
        }else{
            return "";
        }
        url.append("&mainSearchCriteria.v.c=");
        url.append(c);
        // test
        // url.append("250.33");


        return url.toString();
    }

    private ArrayList<MPLinkDTO> parser(final CodeDTO code, ResponseEntity<String> response) {

        ArrayList<MPLinkDTO> mplinks = new ArrayList<MPLinkDTO>();

        if(response.getStatusCode() != HttpStatus.OK) {
            MPLinkDTO mplink = new MPLinkDTO();
            mplink.setCode(code.getCode());
            mplink.setSab(code.getSab());
            mplinks.add(mplink);
            return mplinks;
        }
        
        String r = response.getBody().toString();

        JSONParser parser = new JSONParser();
        String title = "";
        String href = "";
        String rel = "";
        String updated = "";

        try {
            Object obj = parser.parse(r);
    
            JSONObject jo = ((JSONObject)obj);
            jo = (JSONObject)(jo.get("feed"));
                    
            JSONArray array = (JSONArray)(jo.get("entry"));
    
            for(Object o : array){
                MPLinkDTO mplink = new MPLinkDTO();
                mplink.setCode(code.getCode());
                mplink.setSab(code.getSab());
                jo = (JSONObject)(o);
            
                updated = ((JSONObject)(jo.get("updated"))).get("_value").toString();
                JSONArray arr = (JSONArray)(jo.get("link"));
                jo = (JSONObject)(arr.get(0));
            
                title = (jo.get("title")).toString();
                href = (jo.get("href")).toString();
                rel = (jo.get("rel")).toString();
            
                mplink.setTitle(title);
                mplink.setLink(href);
                mplink.setRel(rel);
                mplink.setUpdated(updated);
                mplinks.add(mplink);
            }

            for(MPLinkDTO mp : mplinks)
                logger.info(mp);

        } catch (Exception e) {
            logger.error(e.getMessage());
            MPLinkDTO mplink = new MPLinkDTO();
            mplink.setCode(code.getCode());
            mplink.setSab(code.getSab());
            mplinks.add(mplink);
 
            return mplinks;
        }

        if(mplinks.size() == 0){
            MPLinkDTO mplink = new MPLinkDTO();
            mplink.setCode(code.getCode());
            mplink.setSab(code.getSab());
            mplinks.add(mplink);

        }

        return mplinks;
    }

}
