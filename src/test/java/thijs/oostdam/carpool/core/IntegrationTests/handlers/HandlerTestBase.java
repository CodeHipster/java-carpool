package thijs.oostdam.carpool.core.IntegrationTests.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.core.domain.DomainFactory;
import thijs.oostdam.carpool.core.persistence.CarpoolRepository;
import thijs.oostdam.carpool.core.persistence.SQLUniqueIdGenerator;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

import static org.mockito.Mockito.when;

public abstract class HandlerTestBase {

    protected static JdbcTemplate jdbcTemplate;
    protected static CarpoolRepository carpoolRepository;
    protected static DomainFactory domainFactory;

    @BeforeAll
    public static void beforeAll() throws SQLException {

        DataSource ds = createDatabase();
        Database.applySchema(ds.getConnection(),"core/core-db-schema.xml");
        jdbcTemplate = new JdbcTemplate(ds);
        carpoolRepository = new CarpoolRepository(ds);
        domainFactory = new DomainFactory(new SQLUniqueIdGenerator(ds));
    }

    @AfterEach
    void beforeEach() {
        jdbcTemplate.batchUpdate(
                "DELETE FROM PASSENGERS",
                "DELETE FROM STOP",
                "DELETE FROM TRIP",
                "DELETE FROM PERSON",
                "UPDATE COUNTER SET COUNT = 0"
        );
    }

    private static DataSource createDatabase() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:" + UUID.randomUUID());
        ds.setUser("thijs");
        ds.setPassword("oostdam");
        ds.setCreateDatabase("create");
        return ds;
    }

    /**
     * Mock an HttpExchange
     *
     * @param method
     * @param uri
     * @param data
     * @return
     */
    protected HttpExchange mockHttpExchange(String method, String uri, InputStream data) throws IOException, URISyntaxException {
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getRequestBody()).thenReturn(data);
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getRequestURI()).thenReturn(new URI(uri));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        return exchange;
    }

}
