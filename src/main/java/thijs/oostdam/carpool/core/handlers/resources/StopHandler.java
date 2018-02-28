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
 * @author Thijs Oostdam on 5-7-17.
 */
public class StopHandler extends JsonHandler<TripHttp, TripHttp> {
    private static final Logger LOG = LoggerFactory.getLogger(StopHandler.class);

    private TripService tripService;

    public StopHandler(TripService tripService) {
        super(TripHttp.class);
        this.tripService = tripService;
    }

    @Override
    public TripHttp post(TripHttp trip, List<NameValuePair> queryParams)throws IOException{
        LOG.info("trip to be added: \n{}", trip);
        Preconditions.checkArgument(trip.stops().size() < 2, "Only 1 stop per post supported atm.");
        tripService.addStop(trip.id(), trip.stops().stream().findFirst()
                .orElseThrow(() ->new IllegalArgumentException("A stop is required when adding a stop.")));
        return null;
    }

    @Override
    public TripHttp delete(TripHttp trip, List<NameValuePair> queryParams){
        NameValuePair tripId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("trip-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'trip-id' is required for DELETE method."));

        NameValuePair stopId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("stop-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'passenger-id' is required for DELETE method."));

        tripService.removeStop(Integer.parseInt(tripId.getValue()),Integer.parseInt(stopId.getValue()));
        return null;
    }
}