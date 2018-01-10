package thijs.oostdam.carpool.handlers.resources;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
class TripHandlerTest extends BasehandlerTest{
    private Gson gson = new Gson();

    @Test
    void handlePost() throws IOException, URISyntaxException {
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
        assertThat(insertedTrip.driver().email()).isEqualTo("email@email.com");
        assertThat(insertedTrip.driver().name()).isEqualTo("Firstname Lastname");
        assertThat(insertedTrip.maxPassengers()).isEqualTo(5);
        assertThat(insertedTrip.stops()).extracting("latitude", "longitude", "index", "address").containsExactlyInAnyOrder(
                tuple(1.0,1.0,0, "whatup"),
                tuple(2.0,2.0,1, "whatup2"));
        assertThat(insertedTrip.stops()).extracting("id").isNotIn(0);
        assertThat(insertedTrip.passengers()).extracting("email", "name").containsExactly(
                tuple("email@email.com","Firstname Lastname"));
    }

    @Test
    void testDeleteHandle() throws IOException, URISyntaxException {
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