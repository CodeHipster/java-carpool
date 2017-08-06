package thijs.oostdam.carpool.handlers.resources;

import com.google.common.base.Charsets;
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

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class TripHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TripHandler.class);

    private TripService tripService;
    private Gson gson = new Gson();

    public TripHandler(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = t.getResponseBody();
        try {
            String response;
            if (t.getRequestMethod().equals("POST")) {
                String body = CharStreams.toString(new InputStreamReader(t.getRequestBody(), Charsets.UTF_8));
                LOG.info("trip to be added: \n{}", body);
                ITrip input = new Gson().fromJson(body, TripHttp.class);
                ITrip output = tripService.createTrip(input);
                response = gson.toJson(new TripHttp(output));
            } else if(t.getRequestMethod().equals("GET")){
                List<NameValuePair> queryParams = URLEncodedUtils.parse(t.getRequestURI(), Charsets.UTF_8.name());
                NameValuePair idQuery = queryParams
                        .stream()
                        .filter(q -> q.getName().equalsIgnoreCase("id"))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'id' is required for GET method."));

                //TODO: try parse and throw proper exception.
                Trip output = tripService.findTrip(Integer.parseInt(idQuery.getValue()));
                response = new Gson().toJson(new TripHttp(output));
            }else if(t.getRequestMethod().equals("DELETE")){
                List<NameValuePair> queryParams = URLEncodedUtils.parse(t.getRequestURI(), Charsets.UTF_8.name());
                NameValuePair idQuery = queryParams
                        .stream()
                        .filter(q -> q.getName().equalsIgnoreCase("id"))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("query param 'id' is required for DELETE method."));

                tripService.deleteTrip(Integer.parseInt(idQuery.getValue()));
                response = "";
                //return 204?
            }else{
                response = "we not know your method " + t.getRequestMethod();
            }
            t.sendResponseHeaders(200, response.getBytes().length);
            os.write(response.getBytes());
        } catch (Exception e) {
            LOG.error("something went wrong when creating a trip: {}", e.getMessage(), e);
            t.sendResponseHeaders(500, 0);
        } finally {
            os.close();
        }
    }
}