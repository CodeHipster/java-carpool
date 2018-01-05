package thijs.oostdam.carpool.persistence;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.domain.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
    static void beforeAll() throws SQLException {

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
                "DELETE FROM STOP",
                "DELETE FROM TRIP",
                "DELETE FROM PERSON");
    }

    @Test
    void storeTrip(){
        //insert trip for driver 1
        Person driver1 = domainFactory.person("email1", "name1");
        Collection<Stop> stops = new ArrayList<>();
        stops.add(domainFactory.stop(1,1, "address string", 0));
        stops.add(domainFactory.stop(2,2, "address 2", 1));
        Trip trip1 = domainFactory.trip(
                driver1, stops,
                5,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                fixture.searchTripsByDriverId(driver1.id()));

        fixture.storeTrip(trip1);

        Collection<Trip> trips = fixture.searchTripsByDriverId(driver1.id());
        assertThat(trips.size()).isEqualTo(1);

        //insert trip for driver 2
        Person driver2 = domainFactory.person("email2", "name2");
        stops = new ArrayList<>();
        stops.add(domainFactory.stop(3,3, "address string", 0));
        stops.add(domainFactory.stop(4,4, "address string 2", 1));
        Trip trip2 = domainFactory.trip(
                driver2,
                stops,
                5,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                fixture.searchTripsByDriverId(driver2.id()));

        fixture.storeTrip(trip2);

        trips = fixture.searchTripsByDriverId(driver2.id());
        assertThat(trips.size()).isEqualTo(1);

        //insert another trip for driver 2
        stops = new ArrayList<>();
        stops.add(domainFactory.stop(5,5, "address string", 0));
        stops.add(domainFactory.stop(6,6, "address string 2", 1));
        Trip trip3 = domainFactory.trip(
                driver2,
                stops,
                5,
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS),
                fixture.searchTripsByDriverId(driver2.id()));

        fixture.storeTrip(trip3);

        trips = fixture.searchTripsByDriverId(driver2.id());
        assertThat(trips.size()).isEqualTo(2);

        //insert the same trip again with different stops and a passenger.
        trip3.addPassenger(domainFactory.person("passenger1", "passenger1"), new ArrayList<>());
        trip3.addStop(domainFactory.stop(1,1,"address string 3",2));

        fixture.storeTrip(trip3);

        trips = fixture.searchTripsByDriverId(driver2.id());
        assertThat(trips.size()).isEqualTo(2);
        Trip fetchedTrip3 = trips.stream().filter(trip -> trip.id() == trip3.id()).findFirst().get();
        assertThat(fetchedTrip3.passengers().size()).isEqualTo(2);
        assertThat(fetchedTrip3.stops().size()).isEqualTo(3);
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