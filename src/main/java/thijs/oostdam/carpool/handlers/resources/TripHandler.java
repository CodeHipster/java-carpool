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
import thijs.oostdam.carpool.domain.Trip;
import thijs.oostdam.carpool.domain.interfaces.ITrip;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

import javax.swing.*;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class TripHandler extends JsonHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TripHandler.class);

    private TripService tripService;
    private Gson gson = new Gson();

    public TripHandler(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public String post(HttpExchange exchange) throws IOException{
        String body = CharStreams.toString(new InputStreamReader(exchange.getRequestBody(), Charsets.UTF_8));
        LOG.info("trip to be added: \n{}", body);
        ITrip input = new Gson().fromJson(body, TripHttp.class);
        tripService.createTrip(input);
        return "";
    }

    @Override
    public String delete(HttpExchange exchange){
        List<NameValuePair> queryParams = URLEncodedUtils.parse(exchange.getRequestURI(), Charsets.UTF_8.name());
        NameValuePair idQuery = queryParams
                .stream()
                .filter(q -> q.getName().equalsIgnoreCase("id"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'id' is required for DELETE method."));

        tripService.deleteTrip(Integer.parseInt(idQuery.getValue()));
        return "";
    }
}