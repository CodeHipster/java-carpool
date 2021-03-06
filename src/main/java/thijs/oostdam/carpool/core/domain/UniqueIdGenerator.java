package thijs.oostdam.carpool.core.domain;

/**
 * interface for UniqueIdGenerators
 *
 * @author Thijs Oostdam on 14-7-17.
 */
public interface UniqueIdGenerator {
    /**
     * return a unique id.
     * @return
     */
    int uniqueId();
}
