package thijs.oostdam.carpool.domain.interfaces;

import java.time.Instant;
import java.util.Collection;

/**
 * Created by Thijs on 16-7-2017.
 */
public interface ITrip {
    int id();

    IPerson driver();

    Collection<? extends IStop> stops();

    Collection<? extends IPerson> passengers();

    int maxPassengers();

    Instant departure();

    Instant arrival();
}
