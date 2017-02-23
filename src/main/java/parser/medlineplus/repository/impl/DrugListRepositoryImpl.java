package parser.medlineplus.repository.impl;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

import parser.medlineplus.repository.DrugListRepository;
import parser.medlineplus.model.Drug;

public class DrugListRepositoryImpl implements DrugListRepository {

    public ArrayList<Drug> getDrugList() {

        return new ArrayList<Drug>();

    }
}
