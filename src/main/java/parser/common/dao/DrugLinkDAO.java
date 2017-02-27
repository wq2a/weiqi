package parser.common.dao;

import java.util.*;

import parser.common.model.DrugLink;

public interface DrugLinkDAO {
    // drug link
    public void insert(DrugLink drugLink);
    public ArrayList<String> getRxcui(int start, int range);
    public ArrayList<String> getLink(int start, int range);

    // drug detail info
    public int isDrugPropertyExist(String name);
    public int isDrugEntityExist(String link);
    public void insertDrugEntity (String link);
    public void updateBadDrugEntity (int id);
    public void insertDrugProperty (String name, String desc);
    public void insertDrugValue(int entityId, int propertyId, String value);

}
