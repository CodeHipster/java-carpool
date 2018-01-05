package thijs.oostdam.carpool.handlers.dto;

import thijs.oostdam.carpool.domain.interfaces.IDriver;
import thijs.oostdam.carpool.domain.interfaces.IPassenger;
import thijs.oostdam.carpool.domain.interfaces.IStop;
import thijs.oostdam.carpool.domain.interfaces.ITrip;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Trip object for transfer via http.
 *
 * default with empty values.
 *
 * @author Thijs Oostdam on 5-7-17.
 */
public class TripHttp implements ITrip{
    private int id;
    private int maxPassengers;
    private PersonHttp driver = new PersonHttp();
    private Collection<StopHttp> stops = new ArrayList<>();
    private Collection<PersonHttp> passengers = new ArrayList<>();
    private String departure;
    private String arrival;

    public TripHttp(ITrip trip) {
        this.id = trip.id();
        this.maxPassengers = trip.maxPassengers();
        this.driver = new PersonHttp(trip.driver());
        this.stops = trip.stops().stream().map(StopHttp::new).collect(Collectors.toList());
        this.passengers = trip.passengers().stream().map(PersonHttp::new).collect(Collectors.toList());
        this.departure = trip.departure().toString();
        this.arrival = trip.arrival().toString();
    }

    @Override
    public Instant departure() {
        return Instant.parse(departure);
    }

    @Override
    public Instant arrival() { return Instant.parse(arrival);}

    @Override
    public int id() {
        return id;
    }

    @Override
    public IDriver driver() {
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

}
