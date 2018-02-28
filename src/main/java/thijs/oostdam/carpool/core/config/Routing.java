package thijs.oostdam.carpool.core.config;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.sql.DataSource;
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
