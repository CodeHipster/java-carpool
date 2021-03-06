package thijs.oostdam.carpool.core.UnitTests;

import org.junit.Test;
import thijs.oostdam.carpool.core.domain.OverlapComparator;
import thijs.oostdam.carpool.core.domain.Person;
import thijs.oostdam.carpool.core.domain.Stop;
import thijs.oostdam.carpool.core.domain.Trip;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OverlapComparatorTest {

    @Test
    public void overlap() {
        Person driver = new Person(2, "test", "name");
        List<Stop> newStops = new ArrayList<>();
        newStops.add(new Stop(1, 1, 1, "address", 0));
        newStops.add(new Stop(1, 1, 1, "address", 1));
        Trip trip = new Trip(1, driver, newStops, new ArrayList<>(),5, Instant.parse("2010-01-01T12:10:00Z"), Instant.parse("2010-01-01T13:00:00Z"));

        List<Trip> existingTrips = new ArrayList<>();
        List<Stop> stopsTrip1 = new ArrayList<>();
        stopsTrip1.add(new Stop(1, 1, 1, "address", 0));
        stopsTrip1.add(new Stop(1, 1, 1, "address", 1));
        existingTrips.add(new Trip(1, new Person(2, "test", "name"),stopsTrip1 ,new ArrayList<>(), 5, Instant.parse("2010-01-01T13:10:00Z"), Instant.parse("2010-01-01T14:00:00Z")));
        List<Stop> stopsTrip2 = new ArrayList<>();
        stopsTrip2.add(new Stop(1, 1, 1, "address", 0));
        stopsTrip2.add(new Stop(1, 1, 1, "address", 1));
        existingTrips.add(new Trip(1, new Person(2, "test", "name"),stopsTrip2 ,new ArrayList<>(), 5, Instant.parse("2010-01-01T14:10:00Z"), Instant.parse("2010-01-01T15:00:00Z")));

        boolean overlap = OverlapComparator.overlap(trip, existingTrips);

        assertThat(overlap).isFalse();
    }

    @Test
    public void overlapException() {
        Person driver = new Person(2, "test", "name");
        List<Stop> newStops = new ArrayList<>();
        newStops.add(new Stop(1, 1, 1, "address", 0));
        newStops.add(new Stop(1, 1, 1, "address", 1));
        Trip trip = new Trip(1, driver, newStops, new ArrayList<>(),5, Instant.parse("2010-01-01T12:10:00Z"), Instant.parse("2010-01-01T14:00:00Z"));

        List<Trip> existingTrips = new ArrayList<>();
        List<Stop> stopsTrip1 = new ArrayList<>();
        stopsTrip1.add(new Stop(1, 1, 1, "address", 0));
        stopsTrip1.add(new Stop(1, 1, 1, "address", 1));
        existingTrips.add(new Trip(1, new Person(2, "test", "name"),stopsTrip1 ,new ArrayList<>(), 5, Instant.parse("2010-01-01T13:10:00Z"), Instant.parse("2010-01-01T14:00:00Z")));
        List<Stop> stopsTrip2 = new ArrayList<>();
        stopsTrip2.add(new Stop(1, 1, 1, "address", 0));
        stopsTrip2.add(new Stop(1, 1, 1, "address", 1));
        existingTrips.add(new Trip(1, new Person(2, "test", "name"),stopsTrip2 ,new ArrayList<>(), 5, Instant.parse("2010-01-01T14:10:00Z"), Instant.parse("2010-01-01T15:00:00Z")));

        boolean overlap = OverlapComparator.overlap(trip, existingTrips);

        assertThat(overlap).isTrue();
    }
}