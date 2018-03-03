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
        DataSource dataSourceCore = Database.startCoreDatabase();
        DataSource dataSourceAuth = Database.startAuthDatabase();
        Database.applySchema(dataSourceCore.getConnection(),"core/core-db-schema.xml");
        Database.applySchema(dataSourceAuth.getConnection(),"authentication/authentication-db-schema.xml");

        HttpServer server = Routing.configureRoutes(dataSourceCore);

        LOG.info("starting service on port 8180");
        server.start();
    }
}
