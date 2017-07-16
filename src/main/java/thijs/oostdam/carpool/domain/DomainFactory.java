package thijs.oostdam.carpool.domain;

import java.time.Instant;
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

    public Driver driver(String email, String name){
        return new Driver(idGenerator.uniqueId(), email, name);
    }

    public Passenger passenger(String email, String name){
        return new Passenger(idGenerator.uniqueId(), email, name);
    }

    public Stop stop(double latitude, double longitude, Instant departure){
        return new Stop(idGenerator.uniqueId(), latitude,longitude, departure);
    }

    public Trip trip(Driver driver, Collection<Stop> stops, int maxPassengers, Collection<Trip> driversTrips){
        return Trip.createTrip(idGenerator.uniqueId(), driver, stops, maxPassengers, driversTrips);
    }
}
