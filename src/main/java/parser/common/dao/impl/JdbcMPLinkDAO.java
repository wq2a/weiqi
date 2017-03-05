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


import parser.common.dao.MPLinkDAO;
import parser.common.model.MPLinkDTO;

@Configuration
public class JdbcMPLinkDAO implements MPLinkDAO {
    private static final Logger logger = LogManager.getLogger(JdbcMPLinkDAO.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int isDrugPropertyExist(String name) {
        int id = -1;
        String sql = "SELECT id FROM mp_drug_info_property where name = ? ";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,name);
logger.info(ps);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                id = rs.getInt("id");
            }
            
            ps.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
        }
        logger.info("~~~~~~"+id);
        return id;
    }

    public int isDrugEntityExist(String link) {
        int id = -1;
        String sql = "SELECT id FROM mp_drug_info_entity where rxcui_link = ? ";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,link);

            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                id = rs.getInt("id");
            }
            
            ps.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
        }
        return id;
    }

    public void insertDrugEntity (String link) {
        String sql = "INSERT INTO mp_drug_info_entity "+
            "(rxcui_link)" +
            " VALUES (?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,link);
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

    public void updateBadDrugEntity (int id) {
        String sql = "update mp_drug_info_entity set bad=1 where id = ?";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
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

    public void insertDrugProperty (String name, String desc) {
        String sql = "INSERT INTO mp_drug_info_property "+
            "(name, brief)" +
            " VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,name);
            ps.setString(2,desc.replace("?",""));
            logger.info(ps);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
        }
    }

    public void insertDrugValue(int entityId, int propertyId, String value) {
        String sql = "INSERT INTO mp_drug_info_value "+
            "(entity_id, property_id, value)" +
            " VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,entityId);
            ps.setInt(2,propertyId);
            ps.setString(3,value);
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

    public void insert(MPLinkDTO drugLink) {
        String sql = "INSERT INTO mp_link "+
            "(code, sab, title, link, updated, rel)" +
            " VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,drugLink.getCode());
            ps.setString(2,drugLink.getSab());
            ps.setString(3,drugLink.getTitle());
            ps.setString(4,drugLink.getLink());
            ps.setString(5,drugLink.getUpdated());
            ps.setString(6,drugLink.getRel());
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

    public ArrayList<String> getLink(int start, int range){
        int id = -1;
        String sql = "SELECT link FROM drug_link_sub limit ? , ?";
        Connection conn = null;
        ArrayList<String> dList = new ArrayList<String>();
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,start);
            ps.setInt(2,range);
            logger.info(ps);

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                dList.add(rs.getString("link"));
            }
            
            ps.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
            return dList;
        }

    }

    public ArrayList<String> getRxcui(int start, int range){
        int id = -1;
        String sql = "SELECT rxcui FROM rxcui limit ? , ?";
        Connection conn = null;
        ArrayList<String> dList = new ArrayList<String>();
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,start);
            ps.setInt(2,range);
            logger.info(ps);

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                dList.add(rs.getString("rxcui"));
            }
            
            ps.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try{
                    conn.close();
                }catch(SQLException e) {}
            }
            return dList;
        }

    }
}
