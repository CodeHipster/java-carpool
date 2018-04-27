package thijs.oostdam.carpool.authentication;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.authentication.handlers.LoginHandler;
import thijs.oostdam.carpool.authentication.handlers.RegisterHandler;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.services.KeyPairProvider;
import thijs.oostdam.carpool.authentication.services.PasswordRepository;
import thijs.oostdam.carpool.config.Database;

import javax.sql.DataSource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

//TODO: actually start application?
//provide db connection with parameters

class LoginHandlerIntegrationTest {

    static PasswordRepository repo;

    private static JdbcTemplate template;
    private static AuthenticationService service;

    @Test
    public void testRegisterSucces() throws Exception{
        RegisterHandler registerHandler = new RegisterHandler(service);
        String body = "{email:\"test@test.com\", password:\"test\"}";
        InputStream bodyStream = new ByteArrayInputStream(body.getBytes());
        //test post
        HttpExchange post = mockHttpExchange("POST", "", bodyStream,new Headers());
        registerHandler.handle(post);

        //Assert?
        //login?
    }

    @Test
    void testLoginSucces() throws Exception{
//        //Assign
//        RegisterHandler registerHandler = new RegisterHandler(service);
//        registerHandler.post(new RegistrationHttp("test@test.com", "test"), null);
//
//        LoginHandler loginHandler = new LoginHandler(service);
//        String body = "{email:\"test@test.com\", password:\"test\"}";
//        InputStream bodyStream = new ByteArrayInputStream(body.getBytes());
//        //test post
//        HttpExchange post = mockHttpExchange("POST", "", bodyStream,new Headers());
//
//        //Act
//        loginHandler.handle(post);
//
//        //Assert
//        //what do we expect?
//        //400 no such user
//        Headers responseHeaders = post.getResponseHeaders();
//        OutputStream responseBody = post.getResponseBody();
//        assertThat(post.getResponseCode()).isEqualTo(400);
    }

    @Test
    void testLoginUserDoesNotExist() throws Exception{
        LoginHandler loginHandler = new LoginHandler(service);

        String body = "{email:\"test@test.com\", password:\"test\"}";

        InputStream bodyStream = new ByteArrayInputStream(body.getBytes());
        //test post
        HttpExchange post = mockHttpExchange("POST", "", bodyStream,new Headers());
        loginHandler.handle(post);

        //what do we expect?
        //400 no such user
        Headers responseHeaders = post.getResponseHeaders();
        OutputStream responseBody = post.getResponseBody();
        assertThat(post.getResponseCode()).isEqualTo(400);
    }

    @BeforeAll
    public static void beforeAll() throws SQLException {

        DataSource ds = createDatabase();
        Database.applySchema(ds,"authentication/authenticaton-db-schema.xml");
        repo = new PasswordRepository(ds);
        template = new JdbcTemplate(ds);
        String url = template.getDataSource().getConnection().getMetaData().getURL();


        KeyPairProvider keyPairProvider = new KeyPairProvider();
        service = new AuthenticationService(keyPairProvider, repo);
    }

    @AfterEach
    void afterEach() {
        template.batchUpdate(
                "DELETE FROM authentication"
        );
    }

    private static DataSource createDatabase() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:" + UUID.randomUUID());
        ds.setUser("thijs");
        ds.setPassword("oostdam");
        ds.setCreateDatabase("create");
        return ds;
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