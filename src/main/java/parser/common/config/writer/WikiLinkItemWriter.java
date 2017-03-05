package parser.common.config.writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;

import parser.common.model.WikiLinkDTO;
import parser.common.dao.WikiLinkDAO;

public class WikiLinkItemWriter implements ItemWriter<ArrayList<WikiLinkDTO>> {

    private static final Logger logger = LogManager.getLogger(WikiLinkItemWriter.class);

    private WikiLinkDAO wikilinkDAO;

    public WikiLinkItemWriter(WikiLinkDAO wikilinkDAO) {
        this.wikilinkDAO = wikilinkDAO;
    }

    public void write(List<? extends ArrayList<WikiLinkDTO>> linkset) throws Exception {

        logger.info("Writer");
        logger.info(linkset);
        for(ArrayList<WikiLinkDTO> wikilinks : linkset) {
            for(WikiLinkDTO wikilink : wikilinks) {
                logger.info(wikilink);
                wikilinkDAO.insert(wikilink);
            }
        }
    }

}
