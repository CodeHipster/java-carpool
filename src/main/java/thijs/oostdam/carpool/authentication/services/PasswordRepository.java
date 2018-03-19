package thijs.oostdam.carpool.authentication.services;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import thijs.oostdam.carpool.authentication.domain.PasswordHash;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PasswordRepository {

    private final JdbcTemplate template;
    public PasswordRepository(DataSource ds){
        this.template = new JdbcTemplate(ds);
    }

    public Optional<PasswordHash> getPassword(String email){

        String sql = "Select password-hash, salt from authentication where email = ?";
        return template.query(sql, new Object[]{email}, new PasswordExtractor());
    }

    static class PasswordExtractor implements ResultSetExtractor<Optional<PasswordHash>> {

        @Override
        public Optional<PasswordHash> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            while (resultSet.next()) {
                return Optional.of(new PasswordHash(resultSet.getBytes("password-hash"), resultSet.getBytes("salt")));
            }
            return Optional.empty();
        }
    }
}
