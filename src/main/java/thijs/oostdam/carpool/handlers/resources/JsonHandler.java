package thijs.oostdam.carpool.handlers.resources;

import com.google.common.base.Strings;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * A base handler for all handlers
 *
 * Convenience class(Not proper abstraction).
 * It abstracts the sending of response headers, writing of the response and formatting exceptions.
 *
 * It sets the content-type to "application/json".
 * It sets the response to 200/204 when no exceptions are thrown.
 * 204 when the reponse is null or empty.
 * On exceptions it sets the response to 500 and responds with an json error object "{error:message}".
 * When methods are not implemented it will return 501 (Not Implemented).
 *
 * There are 3 methods you can @Override: get(), post() and delete().
 * default response is 501 (Not Implemented).
 *
 */
public abstract class JsonHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonHandler.class);
    private static final String ERROR_TEMPLATE = "{\"error\":\"{0}\"}";

    @Override
    public final void handle(HttpExchange exchange) throws IOException{
        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();
        try {
            String response;
            switch(exchange.getRequestMethod()){
                case "POST": response = post(exchange); break;
                case "GET": response = get(exchange); break;
                case "DELETE": response = delete(exchange); break;
                default: throw new NotImplementedException();
            }
            if(Strings.isNullOrEmpty(response)){
                exchange.sendResponseHeaders(204, response.getBytes().length);
            }else{
                exchange.getResponseHeaders().add("Content-Type","application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }
            os.write(response.getBytes());
        }catch (NotImplementedException e){
            LOG.warn("Unimplemented method({}) got called.", exchange.getRequestMethod());
            exchange.sendResponseHeaders(501, 0);
        }catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            String response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage());
            exchange.sendResponseHeaders(500, response.getBytes().length);
            os.write(response.getBytes());
        } finally {
            os.close();
        }
    }

    protected String get(HttpExchange exchange)throws IOException{throw new NotImplementedException();}
    protected String post(HttpExchange exchange)throws IOException{throw new NotImplementedException();}
    protected String delete(HttpExchange exchange)throws IOException{throw new NotImplementedException();}
}
