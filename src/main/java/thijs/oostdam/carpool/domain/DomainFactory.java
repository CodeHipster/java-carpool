package thijs.oostdam.carpool.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Thijs on 14-7-2017.
 */
public class DomainFactory {

    private UniqueIdGenerator idGenerator;

    public DomainFactory(UniqueIdGenerator idGenerator){
        this.idGenerator = idGenerator;
    }

    public Person person(String email, String name){
        return new Person(idGenerator.uniqueId(), email, name);
    }

    public Stop stop(double latitude, double longitude, Instant departure){
        return new Stop(idGenerator.uniqueId(), latitude,longitude, departure);
    }
    /**
     * Create a new trip for a person.
     * <p>
     * tripsForDriver will be used to check if there will be overlap with another trip.
     *
     * @param driver,         person to drive the car.
     * @param stops,          places where the trip will stop for sometime.
     * @param driversTrips, all trips the person participates in.
     * @return the newly created trip.
     */
    public Trip trip(Person driver, Collection<Stop> stops, int maxPassengers, Collection<Trip> driversTrips){
        if(OverlapComparator.overlap(stops, driversTrips)){
            throw new IllegalArgumentException("New trip would overlap an existing trip for person: " + driver.email());
        }
        return new Trip(idGenerator.uniqueId(), driver, stops, new ArrayList<>(maxPassengers), maxPassengers);
    }
}
