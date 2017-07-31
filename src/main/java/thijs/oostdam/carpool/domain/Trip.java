package thijs.oostdam.carpool.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import thijs.oostdam.carpool.domain.interfaces.IPassenger;
import thijs.oostdam.carpool.domain.interfaces.IStop;
import thijs.oostdam.carpool.domain.interfaces.ITrip;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A trip is a car with a driver a few passengers and stops where the car will stop.
 *
 * @author Thijs Oostdam on 5-7-17.
 */
//TODO: check that a trip cant have stops in the past?
public class Trip implements ITrip {
    private int id;
    private Driver driver;
    private Collection<Stop> stops;
    private Collection<Passenger> passengers;
    private int maxPassengers;

    /**
     * Construct an existing trip.
     *
     * @param id      of the trip, should be unique among all trips.
     * @param driver, the person driving the car
     * @param stops,  places where the trip will have a stop
     */
    public Trip(int id, Driver driver, Collection<Stop> stops, Collection<Passenger> passengers, int maxPassengers) {
        Preconditions.checkNotNull(driver, "A trip needs a driver.");
        Preconditions.checkNotNull(stops, "A trip like everything else has a beginning and end.");
        Preconditions.checkArgument(stops.size() >= 2, "A trip like everything else has a beginning and end. (meaning atleast 2 stops)");
        Preconditions.checkArgument(maxPassengers > 0, "A trip is not really a trip when there is no room for passengers.");
        Preconditions.checkNotNull(passengers, "A trip needs a list of passengers, could have size 0");

        this.id = id;
        this.maxPassengers = maxPassengers;
        this.driver = driver;
        this.stops = stops;
        this.passengers = passengers;
    }

    /**
     * Create a new trip for a driver.
     * <p>
     * tripsForDriver will be used to check if there will be overlap with another trip.
     *
     * @param driver,         person to drive the car.
     * @param stops,          places where the trip will stop for sometime.
     * @param tripsForDriver, all trips the driver participates in.
     * @return the newly created trip.
     */
    public static Trip createTrip(int id, Driver driver, Collection<Stop> stops, int maxPassengers, Collection<Trip> tripsForDriver) {
        //Stops cannot be empty in the domain.
        Preconditions.checkArgument(stops.size() > 1);
        Instant from = stops.stream().min(Comparator.comparing(Stop::departure)).get().departure();
        Instant to = stops.stream().max(Comparator.comparing(Stop::departure)).get().departure();
        Range<Instant> tripDuration = Range.closed(from, to);

        Optional<Trip> overlappingTrip = tripsForDriver.stream().filter(trip -> {
            Instant existingFrom = trip.stops.stream().min(Comparator.comparing(Stop::departure)).get().departure();
            Instant existingTo = trip.stops.stream().max(Comparator.comparing(Stop::departure)).get().departure();
            Range<Instant> existingTripDuration = Range.closed(existingFrom, existingTo);
            return tripDuration.isConnected(existingTripDuration);
        }).findAny();

        if(overlappingTrip.isPresent()){
            throw new IllegalArgumentException("New trip would overlap an existing trip for driver: " + driver.email());
        }

        return new Trip(id, driver, stops, new ArrayList<>(maxPassengers), maxPassengers);
    }

    public Trip addPassenger(Passenger passenger) {
        if (passengers.size() < maxPassengers) passengers.add(passenger);
        else throw new IllegalStateException("There is no more room for passengers on this trip.");
        return this;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Driver driver() {
        return driver;
    }

    @Override
    public Collection<? extends IStop> stops() {
        return stops;
    }

    @Override
    public Collection<? extends IPassenger> passengers() {
        return passengers;
    }

    @Override
    public int maxPassengers() {
        return maxPassengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
