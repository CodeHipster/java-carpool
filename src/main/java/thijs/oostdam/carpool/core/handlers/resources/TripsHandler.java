package thijs.oostdam.carpool.core.handlers.resources;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.core.domain.Trip;
import thijs.oostdam.carpool.core.handlers.dto.TripHttp;
import thijs.oostdam.carpool.core.services.TripService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thijs Oostdam on 10-7-17.
 */
public class TripsHandler extends JsonHandler<Void, Collection<TripHttp>> {
    private static final Logger LOG = LoggerFactory.getLogger(TripsHandler.class);

    private TripService tripService;

    public TripsHandler(TripService tripService){
        super(Void.class);
        this.tripService = tripService;
    }

    @Override
    public Collection<TripHttp> get(Void body, List<NameValuePair> queryParams) throws IOException{
        Collection<Trip> trips = tripService.getTrips();
        return trips.stream().map(TripHttp::new).collect(Collectors.toList());
    }
}
