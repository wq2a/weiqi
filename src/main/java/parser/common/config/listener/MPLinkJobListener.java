package parser.common.config.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class MPLinkJobListener extends JobExecutionListenerSupport {

    private static final Logger logger = LogManager.getLogger(MPLinkJobListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPLinkJobListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

        // create temp code table
        logger.info("Batch start, create temp table");
        jdbcTemplate.execute("drop table if exists mrconso_icd910_u_code_temp");
        jdbcTemplate.execute("create table mrconso_icd910_u_code_temp select * from mrconso_icd910_u_code_view where CONCAT(code,sab) not in (select CONCAT(code, sab) from mp_link)");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("Batch finished...");

/*            List<Person> results = jdbcTemplate.query("SELECT first_name, last_name FROM people", new RowMapper<Person>() {
                @Override
                public Person mapRow(ResultSet rs, int row) throws SQLException {
                    return new Person(rs.getString(1), rs.getString(2));
                }
            });

            for (Person person : results) {
                logger.info("Found <" + person + "> in the database.");
            }
*/
        }
    }
}
