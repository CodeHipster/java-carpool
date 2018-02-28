package thijs.oostdam.carpool.core.handlers.dto;

import thijs.oostdam.carpool.core.domain.interfaces.IStop;

import java.time.Instant;

/**
 * a stop between start and destination.
 * @author Thijs Oostdam on 5-7-17.
 */
public class StopHttp implements IStop{
    private int id;
    private double latitude;
    private double longitude;
    public String address;
    public int index;

    public StopHttp(IStop iStop) {
        this.id = iStop.id();
        this.latitude = iStop.latitude();
        this.longitude = iStop.longitude();
        this.address = iStop.address();
        this.index = iStop.index();
    }

    @Override
    public int id() {
        return id;
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
    public String address() {return address; }

    @Override
    public int index() {return index; }
}
