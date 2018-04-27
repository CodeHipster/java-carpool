package thijs.oostdam.carpool.authentication.services;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import thijs.oostdam.carpool.authentication.domain.Email;
import thijs.oostdam.carpool.authentication.domain.PasswordHash;
import thijs.oostdam.carpool.authentication.domain.VerificationCode;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PasswordRepository {

    private final JdbcTemplate template;
    public PasswordRepository(DataSource ds){
        this.template = new JdbcTemplate(ds);
    }

    public Optional<PasswordHash> getPassword(Email email){

        String sql = "Select password_hash, salt from authentication where email = ?";
        return template.query(sql, new Object[]{email.email}, new PasswordExtractor());
    }

    public void addPassword(Email email, PasswordHash passwordHash){

        String sql = "Insert into authentication (email, password_hash, salt) VALUES (?,?,?) ";
        template.update(sql, email.email, passwordHash.getHash(), passwordHash.getSalt());
    }

    public void addVerificationCode(VerificationCode code){

        String sql = "Insert into verificationCode (email, code) VALUES (?,?) ";
        template.update(sql, code.email.email, code.code);
    }

    static class PasswordExtractor implements ResultSetExtractor<Optional<PasswordHash>> {

        @Override
        public Optional<PasswordHash> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            while (resultSet.next()) {
                return Optional.of(new PasswordHash(resultSet.getBytes("password_hash"), resultSet.getBytes("salt")));
            }
            return Optional.empty();
        }
    }
}
