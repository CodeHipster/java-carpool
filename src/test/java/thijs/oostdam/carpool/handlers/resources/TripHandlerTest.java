package thijs.oostdam.carpool.handlers.resources;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.domain.DomainFactory;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.persistence.CarpoolRepository;
import thijs.oostdam.carpool.persistence.SQLUniqueIdGenerator;
import thijs.oostdam.carpool.services.TripService;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
class TripHandlerTest {
    private static JdbcTemplate jdbcTemplate;
    private static CarpoolRepository carpoolRepository;
    private static DomainFactory domainFactory;
    private Gson gson = new Gson();

    @BeforeAll
    public static void beforeAll() throws SQLException {

        DataSource ds = createDatabase();
        Database.applySchema(ds.getConnection());
        jdbcTemplate = new JdbcTemplate(ds);
        carpoolRepository = new CarpoolRepository(ds);
        domainFactory = new DomainFactory(new SQLUniqueIdGenerator(ds));
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.batchUpdate(
                "DELETE FROM PASSENGERS",
                "DELETE FROM STOPS",
                "DELETE FROM STOP",
                "DELETE FROM TRIP",
                "DELETE FROM PERSON");
    }

    @Test
    void testHandle() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        TripHandler tripHandler = new TripHandler(tripService);

        //test post
        HttpExchange post = Mockito.mock(HttpExchange.class);

        OutputStream os = new ByteArrayOutputStream();
        when(post.getRequestBody()).thenReturn(Resources.getResource("TripHandlerTest_testHandle.json").openStream());
        when(post.getRequestMethod()).thenReturn("POST");
        when(post.getResponseBody()).thenReturn(os);

        tripHandler.handle(post);
        String response = os.toString();

        assertEquals("{\"id\":4,\"maxPassengers\":5,\"driver\":{\"id\":1,\"email\":\"oostdam@gmail.com\",\"name\":\"Thijs Oostdam\"},\"stops\":[{\"departure\":\"2010-01-01T12:00:00Z\",\"id\":2,\"latitude\":1.0,\"longitude\":1.0},{\"departure\":\"2010-01-01T13:00:00Z\",\"id\":3,\"latitude\":2.0,\"longitude\":2.0}],\"passengers\":[]}", response);

        TripHttp insertedTrip = gson.fromJson(response, TripHttp.class);

        //test get on the created trip
        HttpExchange get = Mockito.mock(HttpExchange.class);
        when(get.getRequestMethod()).thenReturn("GET");
        when(get.getRequestURI()).thenReturn(new URI("http://localhost:8082/trip?id=" + insertedTrip.id()));

        os = new ByteArrayOutputStream();
        when(get.getResponseBody()).thenReturn(os);

        tripHandler.handle(get);
        response = os.toString();

        assertEquals("{\"id\":4,\"maxPassengers\":5,\"driver\":{\"id\":1,\"email\":\"oostdam@gmail.com\",\"name\":\"Thijs Oostdam\"},\"stops\":[{\"departure\":\"2010-01-01T12:00:00Z\",\"id\":2,\"latitude\":1.0,\"longitude\":1.0},{\"departure\":\"2010-01-01T13:00:00Z\",\"id\":3,\"latitude\":2.0,\"longitude\":2.0}],\"passengers\":[]}", response);
    }

    private static DataSource createDatabase() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:CarpoolRepositoryTest");
        ds.setUser("thijs");
        ds.setPassword("oostdam");
        ds.setCreateDatabase("create");
        return ds;
    }
}