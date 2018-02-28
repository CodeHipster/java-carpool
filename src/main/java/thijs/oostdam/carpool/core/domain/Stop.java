package thijs.oostdam.carpool.core.domain;

import java.util.Objects;

import thijs.oostdam.carpool.core.domain.interfaces.IStop;

/**
 * A stop is a place where the trip stands still and passenger can enter or leave the trip.
 *
 * @author Thijs Oostdam on 5-7-17.
 */
public class Stop implements IStop {
    private int id;
    private double latitude;
    private double longitude;
    private String address;
    private int index;

    public Stop(int id, double latitude, double longitude, String address, int index) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.index = index;
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
    public int index(){ return index;}

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
