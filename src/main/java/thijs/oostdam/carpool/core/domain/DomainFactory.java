package thijs.oostdam.carpool.core.domain;

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

    public Stop stop(double latitude, double longitude, String address, int index){
        return new Stop(idGenerator.uniqueId(), latitude,longitude, address, index);
    }

    /**
     * Create a new trip for a driver.
     * <p>
     * tripsForDriver will be used to check if there will be overlap with another trip.
     *
     * Driver will be added as a passenger.
     *
     * @param driver,         person to drive the car.
     * @param stops,          places where the trip will stop for sometime.
     * @param driversTrips, all trips the driver participates in.
     * @return the newly created trip.
     */
    public Trip trip(Person driver, Collection<Stop> stops, int maxPassengers, Instant departure, Instant arrival, Collection<Trip> driversTrips){

        //Add the driver as passenger.
        ArrayList<Person> passengers = new ArrayList<>(maxPassengers);
        passengers.add(driver);
        Trip trip = new Trip(idGenerator.uniqueId(), driver, stops, passengers, maxPassengers, departure, arrival);

        if(OverlapComparator.overlap(trip, driversTrips)){
            //TODO: custom functional exceptions.
            throw new IllegalArgumentException("New trip would overlap an existing trip for person: " + driver.email());
        }

        return trip;
    }
}
