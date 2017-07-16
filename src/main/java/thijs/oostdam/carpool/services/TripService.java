package thijs.oostdam.carpool.services;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import thijs.oostdam.carpool.domain.*;
import thijs.oostdam.carpool.domain.interfaces.ITrip;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
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
        Driver driver = driverOptional.orElse(domainFactory.driver(iTrip.driver().email(), iTrip.driver().name()));

        Collection<Trip> futureTrips = carpoolRepository.searchTripsByDriverId(driver.id());

        Collection<Stop> stops = iTrip.stops().stream()
                .map(iStop -> domainFactory.stop(
                        iStop.latitude(),
                        iStop.longitude(),
                        iStop.departure()))
                .collect(Collectors.toList());

        Trip trip = domainFactory.trip(driver, stops, iTrip.maxPassengers(), futureTrips);

        carpoolRepository.storeTrip(trip);
        return trip;
    }

    public Trip findTrip(int id) {
        return carpoolRepository.searchTrip(id)
                .orElseThrow(() -> new IllegalArgumentException("No trip exists for id: " + id));
    }

    public Collection<Trip> searchTrips() {
        return carpoolRepository.searchTrips();
    }
}
