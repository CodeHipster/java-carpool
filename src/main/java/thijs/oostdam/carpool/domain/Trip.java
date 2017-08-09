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
 * A trip is a car with a person a few passengers and stops where the car will stop.
 *
 * @author Thijs Oostdam on 5-7-17.
 */
//TODO: check that a trip cant have stops in the past?
public class Trip implements ITrip {
    private int id;
    private Person driver;
    private Collection<Stop> stops;
    private Collection<Person> passengers;
    private int maxPassengers;

    /**
     * Construct an existing trip.
     *
     * @param id      of the trip, should be unique among all trips.
     * @param driver, the person driving the car
     * @param stops,  places where the trip will have a stop
     */
    public Trip(int id, Person driver, Collection<Stop> stops, Collection<Person> passengers, int maxPassengers) {
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

    public void addPassenger(Person passenger, Collection<Trip> existingTrips) {
        if (passengers.size() < maxPassengers){
            if(OverlapComparator.overlap(this, existingTrips)){
                throw new IllegalArgumentException("Passenger("+passenger.email()+") already has a trip booked.");
            }
            passengers.add(passenger);
        }
        else throw new IllegalStateException("There is no more room for passengers on this trip.");
    }

    /**
     * Add a stop to the trip.
     * @param stop
     * @param existingTrips for the passengers of this trip.
     */
    public void addStop(Stop stop, Collection<Trip> existingTrips) {
        //Check if new trip would cause overlap.
        this.stops.add(stop);
        if(OverlapComparator.overlap(this, existingTrips)){
            throw new IllegalArgumentException("Stop would cause overlap for one of the participants.");
        }
    }

    public void removeStop(int stopId) {
        Preconditions.checkArgument(stops.size() > 2, "Can't remove stop. A trip needs to have atleast 2 stops.");
        stops = stops.stream().filter(stop -> stop.id() != stopId).collect(Collectors.toList());
    }

    public void removePassenger(int passengerId) {
        passengers = passengers.stream().filter(passenger -> passenger.id() != passengerId).collect(Collectors.toList());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Person driver() {
        return driver;
    }

    @Override
    public Collection<Stop> stops() {
        return stops;
    }

    @Override
    public Collection<Person> passengers() {
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
