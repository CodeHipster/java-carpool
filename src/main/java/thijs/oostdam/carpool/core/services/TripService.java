package thijs.oostdam.carpool.core.services;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import thijs.oostdam.carpool.core.domain.*;
import thijs.oostdam.carpool.core.domain.interfaces.IPerson;
import thijs.oostdam.carpool.core.domain.interfaces.IStop;
import thijs.oostdam.carpool.core.domain.interfaces.ITrip;
import thijs.oostdam.carpool.core.persistence.CarpoolRepository;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class TripService {

    private DomainFactory domainFactory;
    private CarpoolRepository carpoolRepository;

    public TripService(CarpoolRepository carpoolRepository, DomainFactory domainFactory) {
        this.carpoolRepository = carpoolRepository;
        this.domainFactory = domainFactory;
    }

    /**
     * create a trip from given trip interface.
     * Will throw errors if the input in invalid.
     *
     * @param iTrip, the interface for the domain.
     * @return an instantiated domain object.
     */
    public void createTrip(ITrip iTrip) {
        Optional<Person> driverOptional = carpoolRepository.getPerson(iTrip.driver().email());

        Person driver;
        Collection<Trip> existingTrips;
        if(driverOptional.isPresent()){
            driver = driverOptional.get();
            existingTrips = carpoolRepository.searchTripsByDriverId(driver.id());
        }else{
            driver = domainFactory.person(iTrip.driver().email(), iTrip.driver().name());
            existingTrips = new ArrayList<>();
        }

        Collection<Stop> stops = iTrip.stops().stream()
                .map(iStop -> domainFactory.stop(
                        iStop.latitude(),
                        iStop.longitude(),
                        iStop.address(),
                        iStop.index()))
                .collect(Collectors.toList());

        Trip trip = domainFactory.trip(driver, stops, iTrip.maxPassengers(), iTrip.departure(), iTrip.arrival(), existingTrips);

        carpoolRepository.storeTrip(trip);
        return;
    }

    public Collection<Trip> getTrips() {
        return carpoolRepository.getTrips();
    }

    public void deleteTrip(int id) {
        carpoolRepository.deleteTrip(id);
    }

    public void addPassenger(int tripId, IPerson newPassenger) {
        Preconditions.checkNotNull(newPassenger, "A passenger must be given.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newPassenger.email()), "Email of passenger must be given.");
        //check if trip exists.
        Optional<Trip> tripOptional = carpoolRepository.searchTrip(tripId);
        Preconditions.checkArgument(tripOptional.isPresent(), "Trip(%s) must exist before adding passenger(%s)", tripId, newPassenger.email());
        Trip trip = tripOptional.get();

        //check if passenger exists, if not create.
        Optional<Person> optPassenger = carpoolRepository.getPerson(newPassenger.email());
        Person passenger;
        if(optPassenger.isPresent()){
            passenger = optPassenger.get();
            Collection<Trip> trips = carpoolRepository.searchTripsByPassengerId(passenger.id());
            trip.addPassenger(passenger, trips);
        }else{
            //create new person
            passenger = domainFactory.person(newPassenger.email(), newPassenger.name());
            trip.addPassenger(passenger, new ArrayList<>());
        }

        //add to trip
        carpoolRepository.storeTrip(trip);
    }

    public void addStop(int tripId, IStop newStop){
        Preconditions.checkNotNull(newStop, "A stop must be given.");
        //check if trip exists.
        Optional<Trip> tripOptional = carpoolRepository.searchTrip(tripId);
        Preconditions.checkArgument(tripOptional.isPresent(), "Trip(%s) must exist before adding a stop", tripId);
        Trip trip = tripOptional.get();

        Stop stop = domainFactory.stop(newStop.latitude(), newStop.longitude(), newStop.address(), newStop.index());

        trip.addStop(stop);

        carpoolRepository.storeTrip(trip);
    }

    public void removeStop(int tripId, int stopId) {
        Optional<Trip> tripOptional = carpoolRepository.searchTrip(tripId);
        Preconditions.checkArgument(tripOptional.isPresent(), "Trip(%s) must exist before removing a stop", tripId);
        Trip trip = tripOptional.get();

        trip.removeStop(stopId);

        carpoolRepository.storeTrip(trip);
    }

    public void removePassenger(int tripId, int passengerId) {
        Optional<Trip> tripOptional = carpoolRepository.searchTrip(tripId);
        Preconditions.checkArgument(tripOptional.isPresent(), "Trip(%s) must exist before removing a passenger", tripId);
        Trip trip = tripOptional.get();

        trip.removePassenger(passengerId);
        carpoolRepository.storeTrip(trip);
    }
}
