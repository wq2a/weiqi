package parser.common.config.writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.batch.item.ItemWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;


import parser.common.model.MPLinkDTO;
import parser.common.dao.MPLinkDAO;

public class MPLinkItemWriter implements ItemWriter<ArrayList<MPLinkDTO>> {

    private static final Logger logger = LogManager.getLogger(MPLinkItemWriter.class);

    private MPLinkDAO mplinkDAO;

    public MPLinkItemWriter(MPLinkDAO mplinkDAO) {
        this.mplinkDAO = mplinkDAO;
    }

    public void write(List<? extends ArrayList<MPLinkDTO>> linkset) throws Exception {

        logger.info("Writer");
        logger.info(linkset);
        for(ArrayList<MPLinkDTO> mplinks : linkset) {
            for(MPLinkDTO mplink : mplinks) {
                logger.info(mplink);
                mplinkDAO.insert(mplink);
            }
        }
    }

}
