package thijs.oostdam.carpool.authentication.services;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import thijs.oostdam.carpool.authentication.domain.EmailAddress;
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

    public Optional<PasswordHash> getPassword(EmailAddress email){

        String sql = "Select password_hash, salt from authentication where email = ?";
        return template.query(sql, new Object[]{email.address}, new PasswordExtractor());
    }

    public void addPassword(EmailAddress email, PasswordHash passwordHash){

        String sql = "Insert into authentication (email, password_hash, salt, verified) VALUES (?,?,?,?) ";
        template.update(sql, email.address, passwordHash.getHash(), passwordHash.getSalt(), false);
    }

    public void addVerificationCode(VerificationCode code){

        String sql = "Insert into verificationCode (email, code) VALUES (?,?) ";
        template.update(sql, code.email.address, code.code);
    }

    public Optional<VerificationCode> getVerificationCode(VerificationCode code) {

        String sql = "Select email, code from verificationCode where email = ?";
        return template.query(sql, new Object[]{code.email.address}, new verificationCodeExtractor());
    }

    public void removeVerificationCode(VerificationCode code) {

        String sql = "Delete from verificationCode where email = ?";
        template.update(sql, code.email.address);
    }

    public void setVerified(EmailAddress email) {
        String sql = "UPDATE authentication SET verified = true WHERE email = ?";
        template.update(sql, email.address);
    }

    public void updatePassword(EmailAddress email, PasswordHash password) {
        String sql = "update authentication set password_hash = ?, salt = ? where email = ?";
        template.update(sql, password.getHash(), password.getSalt(), email.address);
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
                                new EmailAddress(resultSet.getString("address"))
                                , resultSet.getString("code")));
            }
            return Optional.empty();
        }
    }
}
