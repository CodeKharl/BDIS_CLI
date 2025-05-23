package org.isu_std.dao.mysqldao;

import org.isu_std.dao.UserDao;
import org.isu_std.dao.UserPersonalDao;
import org.isu_std.config.MySQLDBConfig;
import org.isu_std.io.SystemLogger;
import org.isu_std.models.User;
import org.isu_std.models.UserPersonal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MySqlUser implements UserDao, UserPersonalDao {
    @Override
    public int getUserId(String username) {
        String query = "SELECT user_id FROM user WHERE username = ? LIMIT 1";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setString(1, username);

            ResultSet resultSet = preStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean addUser(User user){
        String query = "INSERT INTO user (username, password, barangay_id) values(?, ?, ?)";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setString(1, user.username());
            preStatement.setString(2, user.password());
            preStatement.setInt(3, user.barangayId());

            return preStatement.executeUpdate() == 1;
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return false;
    }

    @Override
    public Optional<User> getOptionalUser(String username){
        String query = "SELECT * FROM user WHERE username = ? LIMIT 1";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setString(1, username);

            ResultSet resultSet = preStatement.executeQuery();
            if(resultSet.next()){
                return Optional.of(getResultUser(resultSet));
            }
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return Optional.empty();
    }

    private User getResultUser(ResultSet resultSet) throws SQLException{
        return new User(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getInt(4)
        );
    }

    @Override
    public Optional<UserPersonal> getOptionalUserPersonal(int userId) {
        String query = "SELECT name, sex, age, birth_date, birth_place, civil_status, nationality, phone_number " +
                "FROM user_personal WHERE user_id = ? LIMIT 1";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setInt(1, userId);

            ResultSet resultSet = preStatement.executeQuery();
            if(resultSet.next()){
                return Optional.of(getResultUserPersonal(resultSet));
            }
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return Optional.empty();
    }

    private UserPersonal getResultUserPersonal(ResultSet resultSet) throws SQLException{
        return new UserPersonal(
                resultSet.getString(1),
                resultSet.getString(2).charAt(0),
                resultSet.getInt(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8)
        );
    }

    @Override
    public boolean addUserPersonal(int userId, UserPersonal userPersonal) {
        String query = "INSERT INTO user_personal(user_id, name, sex, age, " +
                "birth_date, birth_place, civil_status, nationality, phone_number) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setInt(1, userId);
            preStatement.setString(2, userPersonal.name());
            preStatement.setString(3, String.valueOf(userPersonal.sex()));
            preStatement.setInt(4, userPersonal.age());
            preStatement.setString(5, userPersonal.birthDate());
            preStatement.setString(6, userPersonal.birthPlace());
            preStatement.setString(7, userPersonal.civilStatus());
            preStatement.setString(8, userPersonal.nationality());
            preStatement.setString(9, userPersonal.phoneNumber());

            return preStatement.executeUpdate() == 1;
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return false;
    }

    @Override
    public boolean modifyUserPersonal(int userId, String chosenDetail, UserPersonal userPersonal){
        String query = getModifyQuery(chosenDetail);

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            setModifyValues(preStatement, userId, userPersonal);
            return preStatement.executeUpdate() == 1;
        }catch(SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return false;
    }

    private String getModifyQuery(String chosenDetail){
        return "UPDATE user_personal SET %s = ? WHERE user_id = ?"
                .formatted(chosenDetail);
    }

    private void setModifyValues(
            PreparedStatement preStatement, int userId, UserPersonal userPersonal
    ) throws SQLException{
        Object[] values = {
                userPersonal.name(), userPersonal.sex(),
                userPersonal.age(), userPersonal.birthDate(),
                userPersonal.birthPlace(), userPersonal.civilStatus(),
                userPersonal.nationality(), userPersonal.phoneNumber()
        };

        for(Object value : values){
            if(value == null){
                continue;
            }

            String strValue = value.toString().trim();
            if(!strValue.isBlank() && !strValue.equals("0")){
                preStatement.setString(1, strValue);
            }
        }

        preStatement.setInt(2, userId);
    }

    @Override
    public boolean updateUserBarangay(User newUser){
        String query = "UPDATE user SET barangay_id = ? WHERE user_id = ?";

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setInt(1, newUser.barangayId());
            preStatement.setInt(2, newUser.userId());

            return preStatement.executeUpdate() == 1;
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateUserInfo(String chosenDetail, User user){
        String query = getUpdateDetailQuery(chosenDetail);

        try(Connection connection = MySQLDBConfig.getConnection();
            PreparedStatement preStatement = connection.prepareStatement(query);
        ){
            preStatement.setObject(1, getUpdateUserInfo(user));
            preStatement.setInt(2, user.userId());

            return preStatement.executeUpdate() == 1;
        }catch (SQLException e){
            SystemLogger.logWarning(MySqlUser.class, e.getMessage());
        }

        return false;
    }

    private String getUpdateDetailQuery(String chosenDetail){
        return "UPDATE user SET %s = ? WHERE user_id = ?".formatted(chosenDetail);
    }

    private Object getUpdateUserInfo(User user){
        Object[] infos = {user.username(), user.password()};

        for(Object info : infos){
            if(info != null){
                return info;
            }
        }

        throw new IllegalStateException(
                "All attributes of the User object are unset or contain no values."
        );
    }
}
