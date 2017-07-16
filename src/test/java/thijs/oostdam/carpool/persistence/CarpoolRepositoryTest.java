package thijs.oostdam.carpool.persistence;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.domain.DomainFactory;
import thijs.oostdam.carpool.domain.Driver;
import thijs.oostdam.carpool.domain.Stop;
import thijs.oostdam.carpool.domain.Trip;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Thijs on 15-7-2017.
 */
class CarpoolRepositoryTest {

    private static CarpoolRepository fixture;
    private static JdbcTemplate jdbcTemplate;
    private static DomainFactory domainFactory;

    @BeforeAll
    public static void beforeAll() throws SQLException {

        DataSource ds = createDatabase();
        jdbcTemplate = new JdbcTemplate(ds);

        Database.applySchema(ds.getConnection());

        domainFactory = new DomainFactory(new SQLUniqueIdGenerator(ds));

        fixture = new CarpoolRepository(ds);
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
    void storeTrip(){
        //insert trip for driver 1
        Driver driver1 = domainFactory.driver("email1", "name1");
        Collection<Stop> stops = new ArrayList<>();
        stops.add(domainFactory.stop(1,1, Instant.now()));
        stops.add(domainFactory.stop(2,2, Instant.now()));
        Trip trip = domainFactory.trip(driver1, stops, 5, fixture.searchTripsByDriverId(driver1.id()));

        fixture.storeTrip(trip);

        Collection<Trip> trips = fixture.searchTripsByDriverId(driver1.id());
        assertThat(trips.size()).isEqualTo(1);

        //insert trip for driver 2
        Driver driver2 = domainFactory.driver("email2", "name2");
        stops = new ArrayList<>();
        stops.add(domainFactory.stop(3,3, Instant.now()));
        stops.add(domainFactory.stop(4,4, Instant.now()));
        trip = domainFactory.trip(driver2, stops, 5, fixture.searchTripsByDriverId(driver2.id()));

        fixture.storeTrip(trip);

        trips = fixture.searchTripsByDriverId(driver2.id());
        assertThat(trips.size()).isEqualTo(1);

        //insert another trip for driver 2
        stops = new ArrayList<>();
        stops.add(domainFactory.stop(5,5, Instant.now()));
        stops.add(domainFactory.stop(6,6, Instant.now()));
        trip = domainFactory.trip(driver2, stops, 5, fixture.searchTripsByDriverId(driver2.id()));

        fixture.storeTrip(trip);

        trips = fixture.searchTripsByDriverId(driver2.id());
        assertThat(trips.size()).isEqualTo(2);
    }

    //TODO test error messages.

    private static DataSource createDatabase() throws SQLException {

        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:CarpoolRepositoryTest");
        ds.setUser("thijs");
        ds.setPassword("oostdam");
        ds.setCreateDatabase("create");
        return ds;
    }

}