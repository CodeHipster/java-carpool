package thijs.oostdam.carpool.authentication.handlers;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.generic.Response;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;

/**
 * A base handler for all handlers
 *
 * Convenience class(Not a proper abstraction).
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
public abstract class JsonHandler<I,O> implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonHandler.class);
    private static final String ERROR_TEMPLATE = "'{'\"message\":\"{0}\"'}'";


    private static final Gson gson = new Gson();

    private Class clazz;

    protected JsonHandler(Class clazz){
        this.clazz = clazz;
    }

    @Override
    public final void handle(HttpExchange exchange) throws IOException{
        List<NameValuePair> queryParams = URLEncodedUtils.parse(exchange.getRequestURI(), Charsets.UTF_8.name());

        InputStreamReader r = new InputStreamReader(exchange.getRequestBody(), Charsets.UTF_8);
        I object = new Gson().fromJson(r, (Type)clazz);

        //OutputStream is incompatible with java7 try with resources.
        OutputStream os = exchange.getResponseBody();

        //TODO: add headers from response.
        //TODO: differentiate between user error and system errors
        try {
            Response<O> response;
            switch(exchange.getRequestMethod()){
                case "POST": response = post(object, queryParams); break;
                case "GET": response = get(object, queryParams); break;
                case "DELETE": response = delete(object, queryParams); break;
                default: throw new UnsupportedOperationException();
            }
            if(response == null) throw new IllegalArgumentException("Response cannot be null");
            Headers responseHeaders = exchange.getResponseHeaders();
            if(response.getHeaders() != null) {
                for (NameValuePair nvp : response.getHeaders()) {
                    responseHeaders.add(nvp.getName(), nvp.getValue());
                }
            }
            if(response.getBody() == null){
                exchange.sendResponseHeaders(204, -1);
            }else{
                responseHeaders.add("Content-Type","application/json");
                String json = gson.toJson(response);
                exchange.sendResponseHeaders(200, json.getBytes().length);
                os.write(json.getBytes());
            }
        }catch (UnsupportedOperationException e){
            LOG.warn("Unimplemented method({}) got called.", exchange.getRequestMethod());
            exchange.sendResponseHeaders(404, 0);
        }catch (Exception e) {
            LOG.error("Something went wrong: {}", e.getMessage(), e);
            String response = MessageFormat.format(ERROR_TEMPLATE, e.getMessage());
            exchange.sendResponseHeaders(500, response.getBytes().length);
            os.write(response.getBytes());
        } finally {
            os.close();
        }
    }

    protected Response<O> get(I input, List<NameValuePair> queryParams)throws IOException{throw new UnsupportedOperationException();}
    protected Response<O> post(I input, List<NameValuePair> queryParams)throws IOException{throw new UnsupportedOperationException();}
    protected Response<O> delete(I input, List<NameValuePair> queryParams)throws IOException{throw new UnsupportedOperationException();}
}
