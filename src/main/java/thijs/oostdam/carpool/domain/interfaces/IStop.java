package thijs.oostdam.carpool.domain.interfaces;

import java.time.Instant;

/**
 * Created by Thijs on 16-7-2017.
 */
public interface IStop {
    int id();

    Instant departure();

    double latitude();

    double longitude();
}
