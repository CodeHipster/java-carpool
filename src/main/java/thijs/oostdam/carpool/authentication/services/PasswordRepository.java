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

        String sql = "Insert into authentication (email, password_hash, salt, verified) VALUES (?,?,?,?) ";
        template.update(sql, email.email, passwordHash.getHash(), passwordHash.getSalt(), false);
    }

    public void addVerificationCode(VerificationCode code){

        String sql = "Insert into verificationCode (email, code) VALUES (?,?) ";
        template.update(sql, code.email.email, code.code);
    }

    public Optional<VerificationCode> getVerificationCode(VerificationCode code) {

        String sql = "Select email, code from verificationCode where email = ?";
        return template.query(sql, new Object[]{code.email.email}, new verificationCodeExtractor());
    }

    public void removeVerificationCode(VerificationCode code) {

        String sql = "Delete from verificationCode where email = ?";
        template.update(sql, code.email.email);
    }

    public void setVerified(Email email) {
        String sql = "UPDATE authentication SET verified = true WHERE email = ?";
        template.update(sql, email.email);
    }

    public void updatePassword(Email email, PasswordHash password) {
        String sql = "update authentication set password_hash = ?, salt = ? where email = ?";
        template.update(sql, password.getHash(), password.getSalt(), email.email);
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

    private class verificationCodeExtractor implements ResultSetExtractor<Optional<VerificationCode>> {

        @Override
        public Optional<VerificationCode> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            while (resultSet.next()) {
                return Optional.of(
                        new VerificationCode(
                                new Email(resultSet.getString("email"))
                                , resultSet.getString("code")));
            }
            return Optional.empty();
        }
    }
}
