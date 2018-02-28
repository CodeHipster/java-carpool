package thijs.oostdam.carpool.core.handlers.resources;

import com.google.common.base.Preconditions;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.core.handlers.dto.TripHttp;
import thijs.oostdam.carpool.core.services.TripService;

import java.io.IOException;
import java.util.List;

/**
 * @author Thijs Oostdam on 10-7-17.
 */
public class PassengerHandler extends JsonHandler<TripHttp, TripHttp> {
    private static final Logger LOG = LoggerFactory.getLogger(PassengerHandler.class);

    private TripService tripService;

    public PassengerHandler(TripService tripService){
        super(TripHttp.class);
        this.tripService = tripService;
    }

    @Override
    public TripHttp post(TripHttp trip, List<NameValuePair> queryParams) throws IOException{
        LOG.info("Passenger to be added: \n{}", trip);
        Preconditions.checkArgument(trip.passengers().size() < 2, "Only adding 1 passenger at a time is supported.");
        tripService.addPassenger(trip.id(), trip.passengers().stream().findFirst()
                .orElseThrow(()-> new IllegalArgumentException("A passenger is required when adding a new passenger.")));
        return null;
    }

    @Override
    public TripHttp delete(TripHttp exchange, List<NameValuePair> queryParams){
        NameValuePair tripId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("trip-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'trip-id' is required for DELETE method."));

        NameValuePair passengerId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("passenger-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'passenger-id' is required for DELETE method."));

        tripService.removePassenger(Integer.parseInt(tripId.getValue()),Integer.parseInt(passengerId.getValue()));
        return null;
    }
}
