package thijs.oostdam.carpool.handlers.resources;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.domain.Trip;
import thijs.oostdam.carpool.handlers.dto.StopHttp;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

/**
 * @author Thijs Oostdam on 10-7-17.
 */
public class TripsHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TripsHandler.class);

    private TripService tripService;

    public TripsHandler(TripService tripService){
        this.tripService = tripService;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            String response;
            if (t.getRequestMethod().equals("GET")) {
                Collection<Trip> trips = tripService.searchTrips();
                Collection<TripHttp> output = trips.stream().map(TripHttp::new).collect(Collectors.toList());
                response = new Gson().toJson(output);
            } else {
                response = "we not know your method " + t.getRequestMethod();
            }
            OutputStream os = t.getResponseBody();
            t.sendResponseHeaders(200, response.getBytes().length);
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            LOG.error("something went wrong when creating a trip.", e);
        }
    }
}
