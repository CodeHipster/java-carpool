package thijs.oostdam.carpool.core.IntegrationTests.handlers;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;
import thijs.oostdam.carpool.core.handlers.dto.TripHttp;
import thijs.oostdam.carpool.core.handlers.resources.PassengerHandler;
import thijs.oostdam.carpool.core.handlers.resources.TripHandler;
import thijs.oostdam.carpool.core.handlers.resources.TripsHandler;
import thijs.oostdam.carpool.core.services.TripService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class PassengerHandlerTest extends HandlerTestBase {

    private Gson gson = new Gson();

    @Test
    public void handleNewPassenger() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);

        TripHandler tripHandler = new TripHandler(tripService);
        TripsHandler tripsHandler = new TripsHandler(tripService);
        PassengerHandler passengerHandler = new PassengerHandler(tripService);

        //add a trip
        InputStream inputStream = Resources.getResource("trip.json").openStream();
        HttpExchange postTrip = mockHttpExchange("POST", "", inputStream);
        tripHandler.handle(postTrip);

        //get the trip
        HttpExchange get = mockHttpExchange("GET", "http://localhost:8082/trips", new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        String response = get.getResponseBody().toString();
        TripHttp[] insertedTrips = gson.fromJson(response, TripHttp[].class);
        TripHttp insertedTrip = insertedTrips[0];

        //add a passenger
        String data = "{\n" +
                "  \"id\":"+insertedTrip.id()+" ,\n" +
                "  \"passengers\" : [\n" +
                "    {\n" +
                "    \"address\" : \"email2@address.com\",\n" +
                "    \"name\" : \"Some Name 2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        HttpExchange postPassenger = mockHttpExchange("POST", "", new ByteArrayInputStream(data.getBytes()));
        passengerHandler.handle(postPassenger);
        response = postPassenger.getResponseBody().toString();

        assertEquals("", response);

        //fetch the trip
        get = mockHttpExchange("GET", "http://localhost:8082/trips" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        response = get.getResponseBody().toString();

        //assert that passenger is in trip.
        TripHttp[] tripHttp = gson.fromJson(response, TripHttp[].class);
        TripHttp tripWithPassenger = tripHttp[0];
        assertThat(tripWithPassenger.passengers().size()).isEqualTo(2);
        long count = tripWithPassenger.passengers().stream()
                .filter(p -> p.email().equals("email2@address.com") && p.name().equals("Some Name 2")).count();
        assertThat(count).isEqualTo(1);
        count = tripWithPassenger.passengers().stream()
                .filter(p -> p.email().equals("address@address.com") && p.name().equals("Firstname Lastname")).count();
        assertThat(count).isEqualTo(1);
    }
}