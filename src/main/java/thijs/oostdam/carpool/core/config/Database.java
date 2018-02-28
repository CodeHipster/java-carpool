package thijs.oostdam.carpool.core.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    public static DataSource startDatabase() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("carpoolDB");
        ds.setUser("thijs");
        ds.setPassword("oostdam");
        ds.setCreateDatabase("create");
        return ds;
    }

    public static void applySchema(Connection connection) {
        try {
            liquibase.database.Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new liquibase.Liquibase("carpool.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            LOG.error("Exception while trying to apply liquibase schema.",e);
        }
    }
}
