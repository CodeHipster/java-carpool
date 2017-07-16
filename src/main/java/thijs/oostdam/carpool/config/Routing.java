package thijs.oostdam.carpool.config;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.domain.DomainFactory;
import thijs.oostdam.carpool.handlers.resources.PeopleHandler;
import thijs.oostdam.carpool.handlers.resources.PersonHandler;
import thijs.oostdam.carpool.handlers.resources.TripHandler;
import thijs.oostdam.carpool.handlers.resources.TripsHandler;
import thijs.oostdam.carpool.handlers.staticcontent.CssHandler;
import thijs.oostdam.carpool.handlers.staticcontent.HtmlHandler;
import thijs.oostdam.carpool.handlers.staticcontent.JsHandler;
import thijs.oostdam.carpool.persistence.CarpoolRepository;
import thijs.oostdam.carpool.persistence.SQLUniqueIdGenerator;
import thijs.oostdam.carpool.services.TripService;

import javax.sql.DataSource;

/**
 * @author Thijs Oostdam on 6-7-17.
 */
public class Routing {

    public static HttpServer configureRoutes(DataSource dataSource){

        CarpoolRepository carpoolRepository = new CarpoolRepository(dataSource);
        DomainFactory domainFactory = new DomainFactory(new SQLUniqueIdGenerator(dataSource));
        TripService tripService = new TripService(carpoolRepository, domainFactory);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8180), 0);
            server.createContext("/", new HtmlHandler());
            server.createContext("/carpool.js", new JsHandler());
            server.createContext("/carpool.css", new CssHandler());
            server.createContext("/person", new PersonHandler());
            server.createContext("/people", new PeopleHandler());
            server.createContext("/trip", new TripHandler(tripService));
            server.createContext("/trips", new TripsHandler(tripService));
            return server;
        } catch (IOException e) {
            throw new IllegalStateException("Could not start server", e);
        }
    }
}
