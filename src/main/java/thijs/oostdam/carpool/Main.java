package thijs.oostdam.carpool;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import thijs.oostdam.carpool.authentication.services.EmailService;
import thijs.oostdam.carpool.config.CLIConfig;
import thijs.oostdam.carpool.config.Database;
import thijs.oostdam.carpool.config.Routing;
import thijs.oostdam.carpool.email.MailGunEmailService;

import javax.sql.DataSource;


/**
 * @author Thijs Oostdam on 4-7-17.
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        CLIConfig cliConfig = CommandLine.populateCommand(new CLIConfig(), args);

        String mailgunApi = cliConfig.mailgunApi;
        MailGunEmailService emailService = new MailGunEmailService(mailgunApi, "carpool@mg.oostd.am");

        DataSource coreDataSource = Database.startCoreDatabase();
        DataSource authDataSource = Database.startAuthDatabase();
        Database.applySchema(coreDataSource,"core/core-db-schema.xml");
        Database.applySchema(authDataSource,"authentication/authentication-db-schema.xml");

        HttpServer server = Routing.configureRoutes(coreDataSource, new EmailService(emailService));

        LOG.info("starting services on port 8180");
        server.start();

        LOG.info("closing main thread");
    }
}
