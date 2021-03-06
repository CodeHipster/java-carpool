package thijs.oostdam.carpool.core.handlers.staticcontent;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Thijs Oostdam on 5-7-17.
 */
public class CssHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        URL resource = Resources.getResource("carpool.css");
        String response = Resources.toString(resource, Charsets.UTF_8);
        OutputStream os = t.getResponseBody();
        t.sendResponseHeaders(200,response.getBytes().length);
        os.write(response.getBytes());
        os.close();
    }
}