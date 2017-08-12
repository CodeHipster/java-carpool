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
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.domain.interfaces.ITrip;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class StopHandler extends JsonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(StopHandler.class);

    private TripService tripService;

    public StopHandler(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public String post(HttpExchange exchange)throws IOException{
        String body = CharStreams.toString(new InputStreamReader(exchange.getRequestBody(), Charsets.UTF_8));
        LOG.info("trip to be added: \n{}", body);
        ITrip input = new Gson().fromJson(body, TripHttp.class);
        Preconditions.checkArgument(input.stops().size() < 2, "Only 1 stop per post supported atm.");
        tripService.addStop(input.id(), input.stops().stream().findFirst()
                .orElseThrow(() ->new IllegalArgumentException("A stop is required when adding a stop.")));
        return "";
    }

    @Override
    public String delete(HttpExchange exchange){
        List<NameValuePair> queryParams = URLEncodedUtils.parse(exchange.getRequestURI(), Charsets.UTF_8.name());
        NameValuePair tripId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("trip-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'trip-id' is required for DELETE method."));

        NameValuePair stopId = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("stop-id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'passenger-id' is required for DELETE method."));

        tripService.removeStop(Integer.parseInt(tripId.getValue()),Integer.parseInt(stopId.getValue()));
        return "";
    }
}