package thijs.oostdam.carpool.authentication;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.Login;
import thijs.oostdam.carpool.authentication.handlers.*;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.services.EmailService;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO: actually start application?
//provide db connection with parameters?

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationIntegrationTest {

    static PasswordRepository repo;

    private static JdbcTemplate template;
    private static AuthenticationService service;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpHandler dummyHandler;

    @BeforeClass
    public static void beforeAll() throws SQLException {

        DataSource ds = createDatabase();
        Database.applySchema(ds, "authentication/authentication-db-schema.xml");
        repo = new PasswordRepository(ds);
        template = new JdbcTemplate(ds);
    }

    @Before
    public void before() {
        KeyPairProvider keyPairProvider = new KeyPairProvider();
        service = new AuthenticationService(keyPairProvider, repo, emailService);
    }

    @After
    public void afterEach() {
        template.batchUpdate(
                "DELETE FROM authentication"
        );
    }

    /**
     * Happy flow:
     * - register
     * - verify code
     * - login
     * - do authenticated call
     * - reset password
     * - login with new password
     * - change password
     * - login with new password
     */
    @Test
    public void happyFlow() throws Exception {
        RegisterHandler registerHandler = new RegisterHandler(service);
        RegistrationVerificationHandler verificationHandler = new RegistrationVerificationHandler(service);
        LoginHandler loginHandler = new LoginHandler(service);
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(service, dummyHandler);
        ResetPasswordHandler resetPasswordHandler = new ResetPasswordHandler(service);
        ChangePasswordHandler changePasswordHandler = new ChangePasswordHandler(service);

        String password = "test";
        String email = "test@test.com";

        // Register
        String body = "{email:\"" + email + "\", password:\"" + password + "\"}";
        InputStream bodyStream = new ByteArrayInputStream(body.getBytes());
        HttpExchange post = mockHttpExchange("POST", "", bodyStream, new Headers());
        registerHandler.handle(post);

        //intercept verification code
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendVerificationEmail(codeCaptor.capture(), any(Email.class));

        // Verify code
        body = "{email:\"" + email + "\", code:\"" + codeCaptor.getValue() + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        verificationHandler.handle(post);

        //login
        body = "{email:\"" + email + "\", password:\"" + password + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        loginHandler.handle(post);

        Headers responseHeaders = post.getResponseHeaders();
        String loginToken = responseHeaders.get("login-token").get(0);

        // authorized request
        body = "{dummy:\"value\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        Headers headers = new Headers();
        headers.add("login-token", loginToken);
        post = mockHttpExchange("POST", "", bodyStream, headers);
        authenticationFilter.handle(post);

        ArgumentCaptor<HttpExchange> exchangeCaptor = ArgumentCaptor.forClass(HttpExchange.class);
        verify(dummyHandler).handle(exchangeCaptor.capture());
        assertThat(exchangeCaptor.getValue()).isEqualTo(post);

        //reset password
        body = "{email:\"" + email + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        resetPasswordHandler.handle(post);

        //intercept new password
        ArgumentCaptor<Login> passwordCaptor = ArgumentCaptor.forClass(Login.class);
        verify(emailService).sendNewLogin(passwordCaptor.capture());
        String resetPassword = passwordCaptor.getValue().password;

        //login
        body = "{email:\"" + email + "\", password:\"" + resetPassword + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        loginHandler.handle(post);

        responseHeaders = post.getResponseHeaders();
        assertThat(responseHeaders.get("login-token")).isNotEmpty();

        //change password
        String newPassword = "test2";
        body = "{email:\"" + email + "\", oldPassword:\"" + resetPassword + "\", newPassword:\"" + newPassword + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        changePasswordHandler.handle(post);

        //login
        body = "{email:\"" + email + "\", password:\"" + newPassword + "\"}";
        bodyStream = new ByteArrayInputStream(body.getBytes());
        post = mockHttpExchange("POST", "", bodyStream, new Headers());
        loginHandler.handle(post);

        responseHeaders = post.getResponseHeaders();
        assertThat(responseHeaders.get("login-token")).isNotEmpty();
    }

    private static DataSource createDatabase(){
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
    protected HttpExchange mockHttpExchange(String method, String uri, InputStream data, Headers headers) throws URISyntaxException {
        HttpExchange exchange = Mockito.spy(HttpExchange.class);

        OutputStream os = new ByteArrayOutputStream();
        when(exchange.getRequestBody()).thenReturn(data);
        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getResponseBody()).thenReturn(os);
        when(exchange.getRequestURI()).thenReturn(new URI(uri));
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getResponseHeaders()).thenReturn(new Headers());

        return exchange;
    }
}