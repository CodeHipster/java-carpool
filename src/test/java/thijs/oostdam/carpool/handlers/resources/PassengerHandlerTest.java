package thijs.oostdam.carpool.handlers.resources;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PassengerHandlerTest extends BasehandlerTest{

    private Gson gson = new Gson();

    @Test
    void handleNewPassenger() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);

        TripHandler tripHandler = new TripHandler(tripService);
        TripsHandler tripsHandler = new TripsHandler(tripService);
        PassengerHandler passengerHandler = new PassengerHandler(tripService);

        //add a trip
        InputStream inputStream = Resources.getResource("trip.json").openStream();
        HttpExchange postTrip = mockHttpExchange("POST", "", inputStream);
        tripHandler.handle(postTrip);
        String response = postTrip.getResponseBody().toString();
        TripHttp insertedTrip = gson.fromJson(response, TripHttp.class);

        //add a passenger
        String data = "{\n" +
                "  \"id\":"+insertedTrip.id()+" ,\n" +
                "  \"passengers\" : [\n" +
                "    {\n" +
                "    \"email\" : \"email2@email.com\",\n" +
                "    \"name\" : \"Some Name 2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        HttpExchange postPassenger = mockHttpExchange("POST", "", new ByteArrayInputStream(data.getBytes()));
        passengerHandler.handle(postPassenger);
        response = postPassenger.getResponseBody().toString();

        assertEquals("", response);

        //fetch the trip
        HttpExchange get = mockHttpExchange("GET", "http://localhost:8082/trips" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripsHandler.handle(get);
        response = get.getResponseBody().toString();

        //assert that passenger is in trip.
        TripHttp[] tripHttp = gson.fromJson(response, TripHttp[].class);
        assertThat(tripHttp[0].passengers().size()).isEqualTo(1);
        assertThat(tripHttp[0].passengers().stream().findFirst().get().email()).isEqualTo("email2@email.com");
        assertThat(tripHttp[0].passengers().stream().findFirst().get().name()).isEqualTo("Some Name 2");
    }
}