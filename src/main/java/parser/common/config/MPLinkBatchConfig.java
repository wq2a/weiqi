package parser.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
// import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
 
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
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

import parser.common.model.CodeDTO;
import parser.common.model.MPLinkDTO;
import parser.common.dao.MPLinkDAO;
import parser.common.dao.impl.JdbcMPLinkDAO;
import parser.common.config.processor.MPLinkItemProcessor;
import parser.common.config.writer.MPLinkItemWriter;
import parser.common.config.listener.MPLinkJobListener;

@Configuration
@EnableBatchProcessing
public class MPLinkBatchConfig {

    private static final Logger logger = LogManager.getLogger(MPLinkBatchConfig.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<CodeDTO> reader() {

        JdbcPagingItemReader<CodeDTO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(10);

        PagingQueryProvider provider = createQueryProvider();
        reader.setQueryProvider(provider);

        reader.setRowMapper(new BeanPropertyRowMapper<>(CodeDTO.class));

        return reader;
    }

    @Bean
    public MPLinkItemProcessor processor() {
        return new MPLinkItemProcessor();
    }

    @Bean
    public MPLinkItemWriter writer() {
        return new MPLinkItemWriter(mplinkDAO());
    }

    // jobstep
    @Bean
    public Job importMPLinkJob(MPLinkJobListener listener) {
        return jobBuilderFactory.get("importMPLinkJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<CodeDTO, ArrayList<MPLinkDTO>> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public MPLinkDAO mplinkDAO() {
        JdbcMPLinkDAO mplink = new JdbcMPLinkDAO();
        mplink.setDataSource(dataSource);
        return mplink;
    }

    private PagingQueryProvider createQueryProvider() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();

        provider.setSelectClause("select code, sab");
        provider.setFromClause("from mrconso_icd910_u_code_temp");
        provider.setSortKeys(sortByCodeAsc());

        return provider;
    }

    private Map<String, Order> sortByCodeAsc() {
        Map<String, Order> sortConf = new HashMap<>();
        sortConf.put("code", Order.ASCENDING);
        return sortConf;
    }
}
