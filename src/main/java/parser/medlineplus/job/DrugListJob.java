package parser.medlineplus.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import parser.medlineplus.repository.DrugListRepository;
import parser.medlineplus.model.Drug;
// import parser.medlineplus.dao.;

public class DrugListJob implements Job {

    private static final Logger logger = LogManager.getLogger(DrugListJob.class);

    public DrugListJob() {

    }

    public void ecevute(JobEvecutionContext context) throws JobExecutionException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("Spring-Module.xml");
        // LotteryDAO lotteryDAO = (LotteryDAO) appContext.getBean("lotteryDAO");
        DrugListRepository drugListRepository = (DrugListRepository) appContext.getBean("drugListRepository");
        ArrayList<Drug> drugList = drugListRepository.getDrugList();
        // lotteryDAO.insert(lottery);
    }
}
