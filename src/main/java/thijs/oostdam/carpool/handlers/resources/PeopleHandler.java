package thijs.oostdam.carpool.handlers.resources;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class PeopleHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PersonHandler.class);

    public PeopleHandler(){

    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "Not implemented yet.";
        OutputStream os = t.getResponseBody();
        t.sendResponseHeaders(200, response.getBytes().length);
        os.write(response.getBytes());
        os.close();
    }
}