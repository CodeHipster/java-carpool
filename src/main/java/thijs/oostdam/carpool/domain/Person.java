package thijs.oostdam.carpool.domain;

import com.google.common.base.Preconditions;
import thijs.oostdam.carpool.domain.interfaces.IPerson;

import java.util.Objects;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class Person implements IPerson {
    private int id;
    private String email;
    private String name;

    Person(int id, String email, String name){
        Preconditions.checkArgument(email != null && !email.trim().isEmpty(), "Email is required");
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "name is required");
        //TODO: place these checks in the dao?
        Preconditions.checkArgument(email.getBytes().length < 255, "email maxlength is 255");
        Preconditions.checkArgument(name.getBytes().length < 255, "name maxlength is 255");
        this.id = id;
        this.email = email;
        this.name = name;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String email(){
        return email;
    }

    @Override
    public String name(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
