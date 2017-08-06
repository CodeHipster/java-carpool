package thijs.oostdam.carpool.domain;

import org.assertj.core.api.Fail;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OverlapComparatorTest {

    @Test
    void overlap() {
        Driver driver = new Driver(2, "test", "name");
        List<Stop> newStops = new ArrayList<>();
        newStops.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T12:00:00Z")));
        newStops.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T13:00:00Z")));
        Trip trip = new Trip(1, driver, newStops, new ArrayList<>(),5);

        List<Trip> existingTrips = new ArrayList<>();
        List<Stop> stopsTrip1 = new ArrayList<>();
        stopsTrip1.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T13:10:00Z")));
        stopsTrip1.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T14:00:00Z")));
        existingTrips.add(new Trip(1, new Driver(2, "test", "name"),stopsTrip1 ,new ArrayList<>(), 5));
        List<Stop> stopsTrip2 = new ArrayList<>();
        stopsTrip2.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T14:10:00Z")));
        stopsTrip2.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T15:00:00Z")));
        existingTrips.add(new Trip(1, new Driver(2, "test", "name"),stopsTrip2 ,new ArrayList<>(), 5));

        boolean overlap = OverlapComparator.overlap(trip, existingTrips);

        assertThat(overlap).isFalse();
    }

    @Test
    void overlapException() {
        Driver driver = new Driver(2, "test", "name");
        List<Stop> newStops = new ArrayList<>();
        newStops.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T12:00:00Z")));
        newStops.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T13:20:00Z"))); //overlap
        Trip trip = new Trip(1, driver, newStops, new ArrayList<>(),5);

        List<Trip> existingTrips = new ArrayList<>();
        List<Stop> stopsTrip1 = new ArrayList<>();
        stopsTrip1.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T13:10:00Z")));
        stopsTrip1.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T14:00:00Z")));
        existingTrips.add(new Trip(1, new Driver(2, "test", "name"),stopsTrip1 ,new ArrayList<>(), 5));
        List<Stop> stopsTrip2 = new ArrayList<>();
        stopsTrip2.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T14:10:00Z")));
        stopsTrip2.add(new Stop(1, 1, 1, Instant.parse("2010-01-01T15:00:00Z")));
        existingTrips.add(new Trip(1, new Driver(2, "test", "name"),stopsTrip2 ,new ArrayList<>(), 5));


        boolean overlap = OverlapComparator.overlap(trip, existingTrips);

        assertThat(overlap).isTrue();
    }
}