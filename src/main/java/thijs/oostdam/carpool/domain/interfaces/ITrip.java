package thijs.oostdam.carpool.domain.interfaces;

import thijs.oostdam.carpool.domain.Driver;
import thijs.oostdam.carpool.domain.Passenger;
import thijs.oostdam.carpool.domain.Stop;

import java.util.Collection;

/**
 * Created by Thijs on 16-7-2017.
 */
public interface ITrip {
    int id();

    IDriver driver();

    Collection<? extends IStop> stops();

    Collection<? extends IPassenger> passengers();

    int maxPassengers();
}
