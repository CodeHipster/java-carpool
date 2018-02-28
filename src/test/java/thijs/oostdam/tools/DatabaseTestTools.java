package thijs.oostdam.tools;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import thijs.oostdam.carpool.core.config.Database;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by Thijs on 15-7-2017.
 */
public class DatabaseTestTools {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = SetupH2Database();
        Database.applySchema(dataSource.getConnection());
    }

    public static DataSource SetupH2Database() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/test");
        ds.setUser("sa");
        ds.setPassword("sa");
        return ds;
    }

    public static void addTestData(JdbcTemplate template) {
        template.batchUpdate(
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (1, CURRENT_TIMESTAMP,1,1)",
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (2, CURRENT_TIMESTAMP,2,2)",
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (3, CURRENT_TIMESTAMP,3,3)",
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (4, CURRENT_TIMESTAMP,4,4)",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (5, 'email1', 'name1')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (6, 'email2', 'name2')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (7, 'email3', 'name3')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (8, 'email4', 'name4')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (9, 'email5', 'name5')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (10, 'email6', 'name6')",
                "INSERT INTO TRIP (ID, DRIVER_ID , MAX_PASSENGERS ) VALUES (11, 5,4)",
                "INSERT INTO TRIP (ID, DRIVER_ID , MAX_PASSENGERS ) VALUES (12, 8,5)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 1,11)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 2,11)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 3,12)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 4,12)",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 6,11)",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 7,11)",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 9,12)",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 10,12)",
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (14, CURRENT_TIMESTAMP,1,1)",
                "INSERT INTO STOP (ID, DEPARTURE , LATITUDE , LONGITUDE ) VALUES (15, CURRENT_TIMESTAMP,2,2)",
                "INSERT INTO TRIP (ID, DRIVER_ID , MAX_PASSENGERS ) VALUES (16, 5,5)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 14,16)",
                "INSERT INTO STOPS ( STOP_ID , TRIP_ID ) VALUES ( 15,16)",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (17, 'email7', 'name7')",
                "INSERT INTO PERSON (ID, EMAIL , NAME ) VALUES (18, 'email8', 'name8')",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 17,16)",
                "INSERT INTO PASSENGERS( PERSON_ID , TRIP_ID ) VALUES ( 18,16)"

        );
    }
}
