package thijs.oostdam.carpool.services;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import thijs.oostdam.carpool.domain.*;
import thijs.oostdam.carpool.domain.interfaces.IPerson;
import thijs.oostdam.carpool.domain.interfaces.IStop;
import thijs.oostdam.carpool.domain.interfaces.ITrip;
import thijs.oostdam.carpool.persistence.CarpoolRepository;

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
    public Trip createTrip(ITrip iTrip) {
        Optional<Driver> driverOptional = carpoolRepository.getDriver(iTrip.driver().email());

        Driver driver;
        Collection<Trip> existingTrips;
        if(driverOptional.isPresent()){
            driver = driverOptional.get();
            existingTrips = carpoolRepository.searchTripsByDriverId(driver.id());
        }else{
            driver = domainFactory.driver(iTrip.driver().email(), iTrip.driver().name());
            existingTrips = new ArrayList<>();
        }

        Collection<Stop> stops = iTrip.stops().stream()
                .map(iStop -> domainFactory.stop(
                        iStop.latitude(),
                        iStop.longitude(),
                        iStop.departure()))
                .collect(Collectors.toList());

        Trip trip = domainFactory.trip(driver, stops, iTrip.maxPassengers(), existingTrips);

        carpoolRepository.storeTrip(trip);
        return trip;
    }

    public Trip findTrip(int id) {
        return carpoolRepository.searchTrip(id)
                .orElseThrow(() -> new IllegalArgumentException("No trip exists for id: " + id));
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
        Optional<Passenger> optPassenger = carpoolRepository.getPassenger(newPassenger.email());
        Passenger passenger;
        if(optPassenger.isPresent()){
            passenger = optPassenger.get();
            Collection<Trip> trips = carpoolRepository.searchTripsByPassengerId(passenger.id());
            if(OverlapComparator.overlap(trip, trips)){
                throw new IllegalArgumentException("Passenger("+passenger.email()+") already has a trip booked.");
            }
        }else{
            //create new person
            passenger = domainFactory.passenger(newPassenger.email(), newPassenger.name());
            carpoolRepository.addPerson(passenger);
        }

        //add to trip
        carpoolRepository.addPassenger(trip.id(), passenger.id());
    }

    public void addStop(int tripId, IStop newStop){
        Preconditions.checkNotNull(newStop, "A stop must be given.");
        //check if trip exists.
        Optional<Trip> tripOptional = carpoolRepository.searchTrip(tripId);
        Preconditions.checkArgument(tripOptional.isPresent(), "Trip(%s) must exist before adding a stop", tripId);
        Trip trip = tripOptional.get();

        Stop stop = domainFactory.stop(newStop.latitude(), newStop.longitude(), newStop.departure());

        //check if stop is between min and max
        if(!OverlapComparator.inBetween(stop, trip.stops())){
            Collection<Trip> allRelatedTrips = new ArrayList<>();
            allRelatedTrips.addAll(carpoolRepository.searchTripsByDriverId(trip.driver().id()));

            for (Passenger p : trip.passengers()) {
                allRelatedTrips.addAll(carpoolRepository.searchTripsByPassengerId(p.id()));
            }

            //Filter out trip in question.
            allRelatedTrips = allRelatedTrips.stream().filter(t -> t.id() != trip.id()).collect(Collectors.toList());

            //Check if new trip would cause overlap.
            trip.stops().add(stop);
            if(OverlapComparator.overlap(trip, allRelatedTrips)){
                throw new IllegalArgumentException("Stop would cause overlap for one of the participants.");
            }
        }

        carpoolRepository.addStop(tripId, stop);
    }

    public void removeStop(int stopId) {
        carpoolRepository.removeStop(stopId);
    }

    public void removePassenger(int tripId, int passengerId) {
        carpoolRepository.removePassenger(tripId, passengerId);
    }
}
