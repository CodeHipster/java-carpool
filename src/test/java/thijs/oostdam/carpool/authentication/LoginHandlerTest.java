package thijs.oostdam.carpool.authentication;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import thijs.oostdam.carpool.authentication.handlers.LoginHandler;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

class LoginHandlerTest {

    @Test
    void handle() throws Exception{

        //TODO: test logging in and doing a request to the filter.

        KeyPairProvider keyPairProvider = new KeyPairProvider();
        LoginHandler loginHandler = new LoginHandler(keyPairProvider.getKeyPair().getPrivate());

        String body = "{email:\"test@test.com\", password:\"test\"}";

        InputStream bodyStream = new ByteArrayInputStream(body.getBytes());
        //test post
        HttpExchange post = mockHttpExchange("POST", "", bodyStream,new Headers());
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