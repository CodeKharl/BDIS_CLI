package org.isu_std.dao.mysqldao;

import org.isu_std.dao.PaymentDao;
import org.isu_std.config.MySQLDBConfig;
import org.isu_std.io.SystemLogger;
import org.isu_std.models.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MySqlPayment implements PaymentDao {
    @Override
    public boolean addPayment(String referenceId, Payment payment) {
        var query = "INSERT INTO payment(reference_id, payment_type, payment_number, document_cost, payment_date_time) " +
                "VALUES(?, ?, ?, ?, ?)";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setString(1, referenceId);
            preStatement.setString(2, payment.paymentType());
            preStatement.setString(3, payment.paymentNumber());
            preStatement.setDouble(4, payment.documentCost());
            preStatement.setString(5, payment.paymentDateTime());

            return preStatement.executeUpdate() == 1;
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlPayment.class, e.getMessage());
        }

        return false;
    }

    @Override
    public Optional<Payment> getOptionalPayment(String referenceId) {
        String query = "SELECT payment_type, payment_number, document_cost, payment_date_time " +
                "FROM payment WHERE reference_id = ?";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query)
        ){
            preStatement.setString(1, referenceId);

            ResultSet resultSet = preStatement.executeQuery();
            if(resultSet.next()){
                return Optional.of(createPayment(resultSet));
            }
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlPayment.class, e.getMessage());
        }

        return Optional.empty();
    }

    private Payment createPayment(ResultSet resultSet) throws SQLException{
        return new Payment(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getDouble(3),
                resultSet.getString(4)
        );
    }
}
