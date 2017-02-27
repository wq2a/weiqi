package parser.common.repository.impl;

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

import parser.common.repository.DrugListRepository;
import parser.common.model.DrugLink;

public class DrugListRepositoryImpl implements DrugListRepository {

    public DrugLink getDrugLink() {

        return new DrugLink();

    }
}
