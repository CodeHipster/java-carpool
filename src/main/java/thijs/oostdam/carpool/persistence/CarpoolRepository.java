package thijs.oostdam.carpool.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import thijs.oostdam.carpool.domain.Driver;
import thijs.oostdam.carpool.domain.Passenger;
import thijs.oostdam.carpool.domain.Person;
import thijs.oostdam.carpool.domain.Stop;
import thijs.oostdam.carpool.domain.Trip;
import thijs.oostdam.carpool.domain.interfaces.IPerson;
import thijs.oostdam.carpool.domain.interfaces.IStop;
import thijs.oostdam.carpool.domain.interfaces.ITrip;

/**
 * Created by Thijs on 14-7-2017.
 */
public class CarpoolRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CarpoolRepository.class);
    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;
    private String getTripsSql = "SELECT trip.id as tripId, trip.max_passengers as maxPassengers, driver.id as driverId, driver.email as driverEmail" +
            ", driver.name as driverName, stop.id as stopId, stop.longitude, stop.latitude, stop.departure" +
            ", passenger.id as passengerId, passenger.email as passengerEmail, passenger.name as passengerName \n" +
            "FROM trip \n" +
            "INNER JOIN person as driver ON trip.driver_id = driver.id\n" +
            "INNER JOIN stop ON stop.trip_id = trip.id\n" +
            "LEFT JOIN passengers ON trip.id = passengers.trip_id\n" +
            "LEFT JOIN person as passenger ON passengers.person_id = passenger.id\n";

    public CarpoolRepository(DataSource dataSource) {
        this.transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Collection<Trip> getTrips() {
        return jdbcTemplate.query(getTripsSql, new Object[]{}, new TripExtractor());
    }

    public Optional<Trip> searchTrip(int id) {
        String sql = getTripsSql +
                "WHERE trip.id = ?";
        Collection<Trip> query = jdbcTemplate.query(sql, new Object[]{id}, new TripExtractor());

        return query.stream().findFirst();
    }

    public void storeTrip(Trip trip) {
        transactionTemplate.execute(transactionStatus -> {
            //First delete trip.
            deleteTrip(trip.id());
            //Then add again.
            upsertPerson(trip.driver());
            insert(trip);
            trip.passengers().forEach(passenger -> {
                upsertPerson(passenger);
                linkParticipant(trip.id(), passenger.id());
            });
            trip.stops().forEach(stop -> insertStop(stop, trip.id()));
            //TODO: give meaning to return value?
            return true;
        });
    }

    public Optional<Driver> getDriver(String email) {
        List<Driver> query = jdbcTemplate.query("SELECT id, email, name FROM person WHERE email = ?", new Object[]{email}
                , (ResultSet rs, int rowNum) -> new Driver(rs.getInt("id"), rs.getString("email"), rs.getString("name")));

        if (query.isEmpty()) return Optional.empty();
            //TODO warning if more then 1 records returned?
        else return Optional.of(query.get(0));
    }

    public Optional<Passenger> getPassenger(String email) {
        List<Passenger> query = jdbcTemplate.query("SELECT id, email, name FROM person WHERE email = ?", new Object[]{email}
                , (ResultSet rs, int rowNum) -> new Passenger(rs.getInt("id"), rs.getString("email"), rs.getString("name")));

        if (query.isEmpty()) return Optional.empty();
            //TODO warning if more then 1 records returned?
        else return Optional.of(query.get(0));
    }

    public Collection<Trip> searchTripsByDriverId(int driverId) {
        String sql = getTripsSql +
                "WHERE trip.driver_id = ?";
        return jdbcTemplate.query(sql, new Object[]{driverId}, new TripExtractor());
    }

    public Collection<Trip> searchTripsByPassengerId(int passengerId) {
        String sql = getTripsSql +
                "WHERE passenger.id = ?";
        return jdbcTemplate.query(sql, new Object[]{passengerId}, new TripExtractor());
    }

    public void deleteTrip(int tripId){
        String sql = "DELETE FROM TRIP WHERE ID = ?";
        jdbcTemplate.update(sql, tripId);
    }

    private void linkParticipant(int tripId, int passengerId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PASSENGERS WHERE TRIP_ID = ? and PERSON_ID = ?"
                , new Object[]{tripId, passengerId}, Integer.class);
        //TODO: should warn for anything besides count 0 or 1;
        if (count == 0) {
            jdbcTemplate.update("INSERT INTO PASSENGERS (TRIP_ID, PERSON_ID) VALUES (?, ?)"
                    , tripId
                    , passengerId);
        }
    }

    private void insert(ITrip trip) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(ID) FROM TRIP WHERE ID = ?", new Object[]{trip.id()}, Integer.class);

        //TODO: should warn for anything besides count 0 or 1;
        if (count > 0) {
            jdbcTemplate.update("UPDATE TRIP SET DRIVER_ID = ? , MAX_PASSENGERS = ? WHERE ID = ?"
                    , trip.driver().id()
                    , trip.maxPassengers()
                    , trip.id());
        } else {
            jdbcTemplate.update("INSERT INTO TRIP (ID, DRIVER_ID , MAX_PASSENGERS ) VALUES (?, ?, ?)"
                    , trip.id()
                    , trip.driver().id()
                    , trip.maxPassengers());
        }
    }

    private void insertStop(IStop stop, int tripId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(ID) FROM STOP WHERE ID = ?", new Object[]{stop.id()}, Integer.class);

        //TODO: should warn for anything besides count 0 or 1;
        if (count > 0) {
            jdbcTemplate.update("UPDATE STOP SET DEPARTURE = ? , LATITUDE = ?, LONGITUDE = ? WHERE ID = ?"
                    , Timestamp.from(stop.departure())
                    , stop.latitude()
                    , stop.longitude()
                    , stop.id());
        } else {
            jdbcTemplate.update("INSERT INTO STOP (ID, TRIP_ID, DEPARTURE , LATITUDE, LONGITUDE ) VALUES (?, ?, ?, ?, ?)"
                    , stop.id()
                    , tripId
                    , Timestamp.from(stop.departure())
                    , stop.latitude()
                    , stop.longitude());
        }
    }

    private void upsertPerson(IPerson person) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(ID) FROM PERSON WHERE ID = ?", new Object[]{person.id()}, Integer.class);

        //TODO: should warn for anything besides count 0 or 1;
        if (count > 0) {
            jdbcTemplate.update("UPDATE PERSON SET EMAIL = ? , NAME = ? WHERE ID = ?"
                    , person.email()
                    , person.name()
                    , person.id());
        } else {
            jdbcTemplate.update("INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (?, ?, ?)"
                    , person.id()
                    , person.email()
                    , person.name());
        }
    }

    /**
     * TripExtractor
     * <p>
     * extracts trips from a resultset.
     * Trip query returns multiple rows for each trip (stops * passengers)
     * to deduplicate we use sets and the hashcode/equals from the objects.
     */
    static class TripExtractor implements ResultSetExtractor<Collection<Trip>> {

        Set<Trip> trips = new HashSet<>();
        Set<TripDto> tripDtos = new HashSet<>();
        Map<Integer, Driver> driverMap = new HashMap<>();
        Map<Integer, Set<Stop>> stopMap = new HashMap<>();
        Map<Integer, Set<Passenger>> passengersMap = new HashMap<>();

        @Override
        public Collection<Trip> extractData(ResultSet rs) throws SQLException, DataAccessException {
            while (rs.next()) {
                int tripId = rs.getInt("tripId");
                TripDto trip = new TripDto(rs.getInt("tripId"), rs.getInt("maxPassengers"));
                tripDtos.add(trip);
                Driver driver = new Driver(rs.getInt("driverId"), rs.getString("driverEmail"), rs.getString("driverName"));
                driverMap.put(tripId, driver);

                Stop stop = new Stop(rs.getInt("stopId"), rs.getDouble("longitude"), rs.getDouble("latitude"), rs.getTimestamp("departure").toInstant());
                Set<Stop> stops = stopMap.computeIfAbsent(tripId, k -> new HashSet<>());
                stops.add(stop);

                //Passengers are optional and could be null.
                Object passengerId = rs.getObject("passengerId");
                if (passengerId != null) {
                    Passenger passenger = new Passenger(rs.getInt("passengerId"), rs.getString("passengerEmail"), rs.getString("passengerName"));
                    Set<Passenger> passengers = passengersMap.computeIfAbsent(tripId, k -> new HashSet<>());
                    passengers.add(passenger);
                }
            }

            for (TripDto tripDto : tripDtos) {
                Trip trip = new Trip(
                        tripDto.id
                        , driverMap.get(tripDto.id)
                        , stopMap.get(tripDto.id)
                        , passengersMap.computeIfAbsent(tripDto.id, k -> new HashSet<>())
                        , tripDto.maxPassengers);
                trips.add(trip);
            }

            return trips;
        }
    }

    /**
     * simple dto to store trip data from the database.
     */
    private static class TripDto {
        int id;
        int maxPassengers;

        TripDto(int id, int maxPassengers) {
            this.id = id;
            this.maxPassengers = maxPassengers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TripDto tripDto = (TripDto) o;
            return id == tripDto.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
