package parser.common.dao;

import java.util.*;

import parser.common.model.DrugLink;

public interface DrugLinkDAO {
    public void insert(DrugLink drugLink);
    public ArrayList<String> getRxcui(int start, int range);
}
