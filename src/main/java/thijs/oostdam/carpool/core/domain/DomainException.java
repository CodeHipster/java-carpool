package thijs.oostdam.carpool.core.domain;

/**
 * Created by Thijs on 16-7-2017.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message)
    {
        super(message);
    }
}
