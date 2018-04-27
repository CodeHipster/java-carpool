package thijs.oostdam.carpool.config;

import picocli.CommandLine;


@CommandLine.Command(description = "Starts the carpool application.",
        name = "carpool", mixinStandardHelpOptions = true, version = "carpool 1.0")
public class CLIConfig {

    @CommandLine.Option(names = { "--db-core" }, description = "The connection string to use for the core application.")
    public String coredb;

    @CommandLine.Option(names = { "--db-auth" }, description = "The connection string to use for the authentication application.")
    public String authdb;
}
