package thijs.oostdam.carpool.handlers.dto;

import thijs.oostdam.carpool.domain.interfaces.IStop;

import java.time.Instant;

/**
 * a stop between start and destination.
 * @author Thijs Oostdam on 5-7-17.
 */
public class StopHttp implements IStop{
    public String departure;
    private int id;
    private double latitude;
    private double longitude;

    public StopHttp(IStop iStop) {
        this.id = iStop.id();
        this.departure = iStop.departure().toString();
        this.latitude = iStop.latitude();
        this.longitude = iStop.longitude();
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Instant departure() {
        return Instant.parse(departure);
    }

    @Override
    public double latitude() {
        return latitude;
    }

    @Override
    public double longitude() {
        return longitude;
    }
}
