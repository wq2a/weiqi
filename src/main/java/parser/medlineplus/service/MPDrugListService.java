package parser.medlineplus.service;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class MPDrugListService {

    private static final Logger logger = LoggerFactory.getLogger(MPDrugListService.class);

    public MPDrugListService() {

    }

    @Async
    public Future<ArrayList<Drug>> findUser(String druglist) throws InterruptedException {
        logger.info("Looking up " + druglist);
        // https://medlineplus.gov/druginfo/drug_Aa.html
        String url = String.format("https://medlineplus.gov/druginfo/druginfo_%s/.html", user);

        User results = restTemplate.getForObject(url, User.class);

        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);

        return new AsyncResult<>(results);

    }

}
