package thijs.oostdam.carpool.core.IntegrationTests.handlers;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;
import thijs.oostdam.carpool.core.handlers.dto.TripHttp;
import thijs.oostdam.carpool.core.handlers.resources.TripHandler;
import thijs.oostdam.carpool.core.handlers.resources.TripsHandler;
import thijs.oostdam.carpool.core.services.TripService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class TripHandlerTest extends HandlerTestBase {
    private Gson gson = new Gson();

    @Test
    public void handlePost() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        TripHandler tripHandler = new TripHandler(tripService);
        TripsHandler tripsHandler = new TripsHandler(tripService);

        //test post
        HttpExchange post = mockHttpExchange("POST", "", Resources.getResource("trip.json").openStream());
        tripHandler.handle(post);

        //get the trip
        HttpExchange get = mockHttpExchange("GET", "http://localhost:8082/trips", new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        String response = get.getResponseBody().toString();
        TripHttp[] insertedTrips = gson.fromJson(response, TripHttp[].class);
        TripHttp insertedTrip = insertedTrips[0];

        //assert contents of the trip.
        assertThat(insertedTrip.id()).isNotIn(0);
        assertThat(insertedTrip.driver().email()).isEqualTo("address@address.com");
        assertThat(insertedTrip.driver().name()).isEqualTo("Firstname Lastname");
        assertThat(insertedTrip.maxPassengers()).isEqualTo(5);
        assertThat(insertedTrip.stops()).extracting("latitude", "longitude", "index", "address").containsExactlyInAnyOrder(
                tuple(1.0,1.0,0, "whatup"),
                tuple(2.0,2.0,1, "whatup2"));
        assertThat(insertedTrip.stops()).extracting("id").isNotIn(0);
        assertThat(insertedTrip.passengers()).extracting("address", "name").containsExactly(
                tuple("address@address.com","Firstname Lastname"));
    }

    @Test
    public void testDeleteHandle() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        TripHandler tripHandler = new TripHandler(tripService);
        TripsHandler tripsHandler = new TripsHandler(tripService);

        //add trip
        HttpExchange post = mockHttpExchange("POST", "", Resources.getResource("trip.json").openStream());
        tripHandler.handle(post);

        //get the trip
        HttpExchange get = mockHttpExchange("GET", "http://localhost:8082/trips", new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        String response = get.getResponseBody().toString();
        TripHttp[] insertedTrips = gson.fromJson(response, TripHttp[].class);
        TripHttp insertedTrip = insertedTrips[0];

        //delete trip
        HttpExchange delete = mockHttpExchange("DELETE", "http://localhost:8082/trip?id=" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripHandler.handle(delete);

        //test get on the created trip
        get = mockHttpExchange("GET", "http://localhost:8082/trips", new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        response = get.getResponseBody().toString();

        assertThat(response).isEqualTo("[]");
    }
}