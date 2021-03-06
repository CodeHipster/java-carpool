package thijs.oostdam.carpool.config;

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

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    public static DataSource startCoreDatabase() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("coreDB");
        ds.setUser("test");
        ds.setPassword("test");
        ds.setCreateDatabase("create");
        return ds;
    }

    public static DataSource startAuthDatabase() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("authenticationDB");
        ds.setUser("test2");
        ds.setPassword("test2");
        ds.setCreateDatabase("create");
        return ds;
    }

    public static void applySchema(DataSource dataSource, String schema) {
        try {
            liquibase.database.Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            Liquibase liquibase = new liquibase.Liquibase(schema, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            LOG.error("Exception while trying to apply liquibase schema.",e);
        }
    }
}
