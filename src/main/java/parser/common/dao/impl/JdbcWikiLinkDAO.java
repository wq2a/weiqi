package parser.common.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.*;

import parser.common.dao.WikiLinkDAO;
import parser.common.model.WikiLinkDTO;

@Configuration
public class JdbcWikiLinkDAO implements WikiLinkDAO {
    private static final Logger logger = LogManager.getLogger(JdbcWikiLinkDAO.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(WikiLinkDTO wikiLink) {
        String sql = "INSERT INTO wiki_link "+
            "(str, sab, pageid, title, link, updated)" +
            " VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,wikiLink.getStr());
            ps.setString(2,wikiLink.getSab());
            ps.setString(3,wikiLink.getPageid());
            ps.setString(4,wikiLink.getTitle());
            ps.setString(5,wikiLink.getLink());
            ps.setString(6,wikiLink.getUpdated());
            logger.info(ps);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
        }
    }

}
