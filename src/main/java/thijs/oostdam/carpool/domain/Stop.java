package thijs.oostdam.carpool.domain;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.Objects;

import org.jscience.geography.coordinates.LatLong;
import thijs.oostdam.carpool.domain.interfaces.IStop;

/**
 * A stop is a place where the trip stands still and passenger can enter or leave the trip.
 * <p>
 * The start of the trip as well as the destination is a stop.
 *
 * @author Thijs Oostdam on 5-7-17.
 */
public class Stop implements IStop {
    private int id;
    private Instant departure;
    private double latitude;
    private double longitude;

    public Stop(int id, double latitude, double longitude, Instant departure) {
        Preconditions.checkNotNull(departure, "The stop needs to end at some time.");
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.departure = departure;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Instant departure() {
        return departure;
    }

    @Override
    public double latitude() {
        return latitude;
    }

    @Override
    public double longitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return id == stop.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
