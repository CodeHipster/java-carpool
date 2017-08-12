package thijs.oostdam.carpool.handlers.resources;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.domain.Trip;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

/**
 * @author Thijs Oostdam on 10-7-17.
 */
public class TripsHandler extends JsonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TripsHandler.class);

    private TripService tripService;
    private static final Gson gson = new Gson();

    public TripsHandler(TripService tripService){
        this.tripService = tripService;
    }

    @Override
    public String get(HttpExchange exchange) throws IOException{
        Collection<Trip> trips = tripService.getTrips();
        Collection<TripHttp> output = trips.stream().map(TripHttp::new).collect(Collectors.toList());
        return gson.toJson(output);
    }
}
