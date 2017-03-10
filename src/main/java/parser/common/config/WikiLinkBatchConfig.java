package parser.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
 
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import parser.common.model.KeywordDTO;
import parser.common.model.WikiLinkDTO;
import parser.common.dao.WikiLinkDAO;
import parser.common.dao.impl.JdbcWikiLinkDAO;
import parser.common.config.processor.WikiLinkItemProcessor;
import parser.common.config.writer.WikiLinkItemWriter;
import parser.common.config.listener.WikiLinkJobListener;

@Configuration
@EnableBatchProcessing
public class WikiLinkBatchConfig {

    private static final Logger logger = LogManager.getLogger(WikiLinkBatchConfig.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<KeywordDTO> reader() {

        JdbcPagingItemReader<KeywordDTO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(10);

        PagingQueryProvider provider = createQueryProvider();
        reader.setQueryProvider(provider);

        reader.setRowMapper(new BeanPropertyRowMapper<>(KeywordDTO.class));

        return reader;
    }

    @Bean
    public WikiLinkItemProcessor processor() {
        return new WikiLinkItemProcessor();
    }

    @Bean
    public WikiLinkItemWriter writer() {
        return new WikiLinkItemWriter(wikilinkDAO());
    }

    // jobstep
    @Bean
    public Job importWikiLinkJob(WikiLinkJobListener listener) {
        return jobBuilderFactory.get("importWikiLinkJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(wikiStep())
                .end()
                .build();
    }

    @Bean
    public Step wikiStep() {
        return stepBuilderFactory.get("wikiStep")
                .<KeywordDTO, ArrayList<WikiLinkDTO>> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public WikiLinkDAO wikilinkDAO() {
        JdbcWikiLinkDAO wikilink = new JdbcWikiLinkDAO();
        wikilink.setDataSource(dataSource);
        return wikilink;
    }

    private PagingQueryProvider createQueryProvider() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();

        provider.setSelectClause("select str, 'RXNORM' as sab");
        provider.setFromClause("from drug_str_temp");
        //provider.setSelectClause("select str, 'RXNORM' as sab");
        //provider.setFromClause("from drug_str_view");
        //provider.setSelectClause("select str, sab");
        //provider.setFromClause("from mrconso_icd910_u_str_temp");
        provider.setSortKeys(sortByCodeAsc());

        return provider;
    }

    private Map<String, Order> sortByCodeAsc() {
        Map<String, Order> sortConf = new HashMap<>();
        sortConf.put("str", Order.ASCENDING);
        return sortConf;
    }
}
