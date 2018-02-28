package thijs.oostdam.carpool.core.handlers.resources;

import com.google.gson.Gson;
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
public class TripHandler extends JsonHandler<TripHttp, TripHttp> {
    private static final Logger LOG = LoggerFactory.getLogger(TripHandler.class);

    private TripService tripService;
    private Gson gson = new Gson();

    public TripHandler(TripService tripService) {
        super(TripHttp.class);
        this.tripService = tripService;
    }

    @Override
    public TripHttp post(TripHttp trip, List<NameValuePair> queryParams) throws IOException{
        LOG.info("trip to be added: \n{}", trip);
        tripService.createTrip(trip);
        return null;
    }

    @Override
    public TripHttp delete(TripHttp trip, List<NameValuePair> queryParams){
        NameValuePair idQuery = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'id' is required for DELETE method."));

        tripService.deleteTrip(Integer.parseInt(idQuery.getValue()));
        return null;
    }
}