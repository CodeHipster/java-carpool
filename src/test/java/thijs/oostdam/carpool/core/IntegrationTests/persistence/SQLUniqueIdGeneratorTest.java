package thijs.oostdam.carpool.core.IntegrationTests.persistence;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.core.persistence.SQLUniqueIdGenerator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Thijs Oostdam on 14-7-17.
 */
class SQLUniqueIdGeneratorTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void beforeAll() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:testDb");
        ds.setUser("test");
        ds.setPassword("test");
        ds.setCreateDatabase("create");

        Database.applySchema(ds,"core/core-db-schema.xml");

        dataSource = ds;
    }

    @Test
    void uniqueId() {
        SQLUniqueIdGenerator sqlUniqueIdGenerator = new SQLUniqueIdGenerator(dataSource);
        int id = sqlUniqueIdGenerator.uniqueId();

        Assert.isTrue(1 == id);

        id = sqlUniqueIdGenerator.uniqueId();
        Assert.isTrue(2 == id);
    }

}