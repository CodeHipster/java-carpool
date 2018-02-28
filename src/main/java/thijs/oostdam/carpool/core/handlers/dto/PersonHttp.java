package thijs.oostdam.carpool.core.handlers.dto;

import thijs.oostdam.carpool.core.domain.interfaces.IDriver;
import thijs.oostdam.carpool.core.domain.interfaces.IPassenger;
import thijs.oostdam.carpool.core.domain.interfaces.IPerson;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class PersonHttp implements IDriver, IPassenger{
    public int id;
    public String email;
    public String name;

    public PersonHttp(IPerson person) {
        this.id = person.id();
        this.email = person.email();
        this.name = person.name();
    }

    public PersonHttp() {
        //set default values
        this.email = "";
        this.name = "";
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String name() {
        return name;
    }
}
