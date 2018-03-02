package thijs.oostdam.carpool.jwt.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

class LoginHandlerTest {

    @Test
    void handle() throws Exception{

        LoginHandler loginHandler = new LoginHandler();

        String initialString = "{email:\"test@test.com\", password:\"test\"}";

        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        //test post
        HttpExchange post = mockHttpExchange("POST", "", targetStream,new Headers());
        loginHandler.handle(post);
    }



    /**
     * Mock an HttpExchange
     *
     * @param method
     * @param uri
     * @param data
     * @return
     */
    protected HttpExchange mockHttpExchange(String method, String uri, InputStream data, Headers headers) throws IOException, URISyntaxException {
        HttpExchange exchange = Mockito.mock(HttpExchange.class);

        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getRequestBody()).thenReturn(data);
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getRequestURI()).thenReturn(new URI(uri));
        when(exchange.getResponseHeaders()).thenReturn(headers);

        return exchange;
    }
}