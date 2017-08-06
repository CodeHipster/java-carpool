package thijs.oostdam.carpool.handlers.resources;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import thijs.oostdam.carpool.handlers.dto.TripHttp;
import thijs.oostdam.carpool.services.TripService;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
class TripHandlerTest extends BasehandlerTest{
    private Gson gson = new Gson();

    @Test
    void handlePost() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        TripHandler tripHandler = new TripHandler(tripService);

        //test post
        HttpExchange post = mockHttpExchange("POST", "", Resources.getResource("TripHandlerTest_handlePost.json").openStream());
        tripHandler.handle(post);
        String response = post.getResponseBody().toString();

        assertEquals("{\"id\":4,\"maxPassengers\":5,\"driver\":{\"id\":1,\"email\":\"oostdam@gmail.com\",\"name\":\"Thijs Oostdam\"},\"stops\":[{\"departure\":\"2010-01-01T12:00:00Z\",\"id\":2,\"latitude\":1.0,\"longitude\":1.0},{\"departure\":\"2010-01-01T13:00:00Z\",\"id\":3,\"latitude\":2.0,\"longitude\":2.0}],\"passengers\":[]}", response);

        TripHttp insertedTrip = gson.fromJson(response, TripHttp.class);

        //test get on the created trip
        HttpExchange get = mockHttpExchange("GET","http://localhost:8082/trip?id=" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripHandler.handle(get);
        response = get.getResponseBody().toString();

        assertEquals("{\"id\":4,\"maxPassengers\":5,\"driver\":{\"id\":1,\"email\":\"oostdam@gmail.com\",\"name\":\"Thijs Oostdam\"},\"stops\":[{\"departure\":\"2010-01-01T12:00:00Z\",\"id\":2,\"latitude\":1.0,\"longitude\":1.0},{\"departure\":\"2010-01-01T13:00:00Z\",\"id\":3,\"latitude\":2.0,\"longitude\":2.0}],\"passengers\":[]}", response);
    }

    @Test
    void testDeleteHandle() throws IOException, URISyntaxException {
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        TripHandler tripHandler = new TripHandler(tripService);

        //add trip
        HttpExchange post = mockHttpExchange("POST", "", Resources.getResource("TripHandlerTest_handlePost.json").openStream());
        tripHandler.handle(post);
        String response = post.getResponseBody().toString();

        assertEquals("{\"id\":4,\"maxPassengers\":5,\"driver\":{\"id\":1,\"email\":\"oostdam@gmail.com\",\"name\":\"Thijs Oostdam\"},\"stops\":[{\"departure\":\"2010-01-01T12:00:00Z\",\"id\":2,\"latitude\":1.0,\"longitude\":1.0},{\"departure\":\"2010-01-01T13:00:00Z\",\"id\":3,\"latitude\":2.0,\"longitude\":2.0}],\"passengers\":[]}", response);

        TripHttp insertedTrip = gson.fromJson(response, TripHttp.class);

        //delete trip
        HttpExchange delete = mockHttpExchange("DELETE", "http://localhost:8082/trip?id=" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripHandler.handle(delete);

        //test get on the created trip
        HttpExchange get = mockHttpExchange("GET","http://localhost:8082/trip?id=" + insertedTrip.id(), new ByteArrayInputStream("".getBytes()));
        tripHandler.handle(get);
        response = get.getResponseBody().toString();

        assertEquals("", response);
    }
}