package thijs.oostdam.carpool.handlers.dto;

import thijs.oostdam.carpool.domain.interfaces.IDriver;
import thijs.oostdam.carpool.domain.interfaces.IPassenger;
import thijs.oostdam.carpool.domain.interfaces.IStop;
import thijs.oostdam.carpool.domain.interfaces.ITrip;

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

    public TripHttp(ITrip output) {
        this.id = output.id();
        this.maxPassengers = output.maxPassengers();
        this.driver = new PersonHttp(output.driver());
        this.stops = output.stops().stream().map(StopHttp::new).collect(Collectors.toList());
        this.passengers = output.passengers().stream().map(PersonHttp::new).collect(Collectors.toList());
    }

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
