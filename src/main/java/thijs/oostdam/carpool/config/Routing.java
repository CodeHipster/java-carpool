package thijs.oostdam.carpool.config;

import com.sun.net.httpserver.HttpServer;
import thijs.oostdam.carpool.authentication.handlers.*;
import thijs.oostdam.carpool.authentication.services.AuthenticationService;
import thijs.oostdam.carpool.authentication.services.EmailService;
import thijs.oostdam.carpool.authentication.services.KeyPairProvider;
import thijs.oostdam.carpool.authentication.services.PasswordRepository;
import thijs.oostdam.carpool.core.domain.DomainFactory;
import thijs.oostdam.carpool.core.handlers.resources.PassengerHandler;
import thijs.oostdam.carpool.core.handlers.resources.StopHandler;
import thijs.oostdam.carpool.core.handlers.resources.TripHandler;
import thijs.oostdam.carpool.core.handlers.resources.TripsHandler;
import thijs.oostdam.carpool.core.handlers.staticcontent.CssHandler;
import thijs.oostdam.carpool.core.handlers.staticcontent.HtmlHandler;
import thijs.oostdam.carpool.core.handlers.staticcontent.JsHandler;
import thijs.oostdam.carpool.core.persistence.CarpoolRepository;
import thijs.oostdam.carpool.core.persistence.SQLUniqueIdGenerator;
import thijs.oostdam.carpool.core.services.TripService;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class Routing {

    public static HttpServer configureRoutes(DataSource dataSource, EmailService emailService){

        CarpoolRepository carpoolRepository = new CarpoolRepository(dataSource);
        PasswordRepository passwordRepository = new PasswordRepository(dataSource);
        DomainFactory domainFactory = new DomainFactory(new SQLUniqueIdGenerator(dataSource));
        TripService tripService = new TripService(carpoolRepository, domainFactory);
        KeyPairProvider keyPairProvider = new KeyPairProvider();
        AuthenticationService authenticationService = new AuthenticationService(keyPairProvider, passwordRepository, emailService);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8180), 0);
            server.createContext("/", new HtmlHandler());
            server.createContext("/statics/core/carpool.js", new JsHandler());
            server.createContext("/statics/core/carpool.css", new CssHandler());
            server.createContext("/auth/login", new LoginHandler(authenticationService));
            server.createContext("/auth/register", new RegisterHandler(authenticationService));
            //Got to go through the authentication filter, as we only want authenticated people changing their password :P
            server.createContext("/auth/change-password", new AuthenticationFilter(authenticationService, new ChangePasswordHandler(authenticationService)));
            server.createContext("/auth/verify", new RegistrationVerificationHandler(authenticationService));
            server.createContext("/auth/reset-password", new ResetPasswordHandler(authenticationService));

            server.createContext("/trip", new TripHandler(tripService));
            server.createContext("/trips", new TripsHandler(tripService));
            server.createContext("/trip/passenger", new PassengerHandler(tripService));
            server.createContext("/trip/stop", new StopHandler(tripService));
            return server;
        } catch (IOException e) {
            throw new IllegalStateException("Could not start server", e);
        }
    }
}
