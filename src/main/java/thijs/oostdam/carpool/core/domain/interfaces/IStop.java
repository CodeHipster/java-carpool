package thijs.oostdam.carpool.core.domain.interfaces;

import java.time.Instant;

/**
 * Created by Thijs on 16-7-2017.
 */
public interface IStop {
    int id();

    double latitude();

    double longitude();

    // human readable address
    String address();

    // which stop in the trip it is.
    int index();
}
