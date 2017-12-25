package thijs.oostdam.carpool;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.config.Routing;

import javax.sql.DataSource;


/**
 * @author Thijs Oostdam on 4-7-17.
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        DataSource dataSource = Database.startDatabase();
        Database.applySchema(dataSource.getConnection());

        HttpServer server = Routing.configureRoutes(dataSource);

        LOG.info("starting service on port 8180");
        server.start();
    }
}
