package thijs.oostdam.carpool.core.persistence;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import thijs.oostdam.carpool.core.domain.UniqueIdGenerator;

import javax.sql.DataSource;
import java.util.List;

/**
 * Class to generate unique id's
 *
 * @author Thijs Oostdam on 14-7-17.
 */
public class SQLUniqueIdGenerator implements UniqueIdGenerator {

    private TransactionTemplate transactionTemplate;
    private JdbcTemplate jdbcTemplate;

    public SQLUniqueIdGenerator(DataSource ds){

        transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
        jdbcTemplate = new JdbcTemplate(ds);

        try{
            List<Integer> exist = jdbcTemplate.query("SELECT count from COUNTER FETCH FIRST ROW ONLY", (rs, i) -> rs.getInt("count"));

            if(exist.size() != 1){
                throw new IllegalStateException("COUNTER table does not have correct amount of rows. Should be 1 is: + " + exist.size());
            }
        }catch (DataAccessException dae){
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    jdbcTemplate.execute("CREATE TABLE COUNTER(count BIGINT)");
                    //TODO: do something with nr of rows modified?
                    jdbcTemplate.update("INSERT INTO COUNTER (count) VALUES (0)");
                }
            });
        }
    }

    @Override
    public int uniqueId() {
        return transactionTemplate.execute(transactionStatus -> {
            jdbcTemplate.execute("UPDATE COUNTER SET count = (SELECT count FROM COUNTER FETCH FIRST ROW ONLY) + 1");
            return jdbcTemplate.queryForObject("SELECT count FROM COUNTER FETCH FIRST ROW ONLY",(rs, rowNum) -> rs.getInt("count"));
        });

    }
}
