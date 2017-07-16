package thijs.oostdam.carpool.domain;

import thijs.oostdam.carpool.domain.interfaces.IPassenger;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class Passenger extends Person implements IPassenger{
    public Passenger(int id, String email, String name) {
        super(id, email, name);
    }
}
