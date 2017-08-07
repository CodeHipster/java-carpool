package thijs.oostdam.carpool.handlers.resources;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.domain.interfaces.ITrip;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

/**
 * @author Thijs Oostdam on 10-7-17.
 */
public class PassengerHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PassengerHandler.class);

    private TripService tripService;

    public PassengerHandler(TripService tripService){
        this.tripService = tripService;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            String response;
            if (t.getRequestMethod().equals("POST")) {
                String body = CharStreams.toString(new InputStreamReader(t.getRequestBody(), Charsets.UTF_8));
                LOG.info("Passenger to be added: \n{}", body);
                ITrip trip = new Gson().fromJson(body, TripHttp.class);
                Preconditions.checkArgument(trip.passengers().size() < 2, "Only adding 1 passenger at a time is supported.");
                tripService.addPassenger(trip.id(), trip.passengers().stream().findFirst()
                        .orElseThrow(()-> new IllegalArgumentException("A passenger is required when adding a new passenger.")));
                response = "";
            }else if(t.getRequestMethod().equals("DELETE")){
                List<NameValuePair> queryParams = URLEncodedUtils.parse(t.getRequestURI(), Charsets.UTF_8.name());
                NameValuePair tripId = queryParams
                        .stream()
                        .filter(q -> q.getName().equalsIgnoreCase("trip-id"))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'trip-id' is required for DELETE method."));

                NameValuePair passengerId = queryParams
                        .stream()
                        .filter(q -> q.getName().equalsIgnoreCase("passenger-id"))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'passenger-id' is required for DELETE method."));

                tripService.removePassenger(Integer.parseInt(tripId.getValue()),Integer.parseInt(passengerId.getValue()));
                response = "";
                //return 204?
            } else {
                response = "we not know your method " + t.getRequestMethod();
            }
            OutputStream os = t.getResponseBody();
            t.getResponseHeaders().add("Content-Type","application/json");
            t.sendResponseHeaders(200, response.getBytes().length);
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            LOG.error("something went wrong when creating a trip.", e);
        }
    }
}
