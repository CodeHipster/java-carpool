package thijs.oostdam.carpool.handlers.resources;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PassengerHandlerTest extends BasehandlerTest{

    private Gson gson = new Gson();

    @Test
    void handleNewPassenger() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);

        TripHandler tripHandler = new TripHandler(tripService);
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
        HttpExchange get = mockHttpExchange("GET", "http://localhost:8082/trip?id=" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripHandler.handle(get);
        response = get.getResponseBody().toString();

        //assert that passenger is in trip.
        TripHttp tripHttp = gson.fromJson(response, TripHttp.class);
        assertThat(tripHttp.passengers().size()).isEqualTo(1);
        assertThat(tripHttp.passengers().stream().findFirst().get().email()).isEqualTo("email2@email.com");
        assertThat(tripHttp.passengers().stream().findFirst().get().name()).isEqualTo("Some Name 2");
    }
}