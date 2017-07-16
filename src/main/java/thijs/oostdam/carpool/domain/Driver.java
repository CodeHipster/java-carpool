package thijs.oostdam.carpool.domain;

import thijs.oostdam.carpool.domain.interfaces.IDriver;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class Driver extends Person implements IDriver{
    public Driver(int id, String email, String name) {
        super(id, email, name);
    }
}
