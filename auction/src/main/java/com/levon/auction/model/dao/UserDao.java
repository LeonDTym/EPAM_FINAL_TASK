package com.levon.auction.model.dao;

import com.levon.auction.exception.DaoException;
import com.levon.auction.model.entity.User;
import com.levon.auction.model.entity.UserRole;
import com.levon.auction.model.entity.UserStatus;
import com.levon.auction.model.entity.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao extends AbstractDao<User> {

    private static final Logger logger = LogManager.getLogger();
    private static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_INSERT_USER = "INSERT INTO users (email, password, first_name, last_name, balance, status, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER_BY_ID = "UPDATE users SET email = ?, password = ?, first_name = ?, last_name = ?, balance = ?, status = ?, role = ? WHERE user_id = ?";

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_USERS);

            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong(ColumnName.USER_ID));
                user.setEmail(resultSet.getString(ColumnName.USER_EMAIL));
                user.setPassword(resultSet.getString(ColumnName.USER_PASSWORD));
                user.setFirstName(resultSet.getString(ColumnName.USER_FIRST_NAME));
                user.setLastName(resultSet.getString(ColumnName.USER_LAST_NAME));
                user.setBalance(resultSet.getBigDecimal(ColumnName.USER_BALANCE));
                user.setStatus(UserStatus.valueOf(resultSet.getString(ColumnName.USER_STATUS)));
                user.setRole(UserRole.valueOf(resultSet.getString(ColumnName.USER_ROLE)));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new DaoException("Couldn't execute query " + SQL_SELECT_ALL_USERS, e);
        }
        logger.log(Level.INFO, "Return list of users with size: " + users.size());
        return users;
    }

    @Override
    public Optional<User> findEntityById(Long id) throws DaoException {
        Optional<User> optionalUser;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_ID)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong(ColumnName.USER_ID));
                user.setEmail(resultSet.getString(ColumnName.USER_EMAIL));
                user.setPassword(resultSet.getString(ColumnName.USER_PASSWORD));
                user.setFirstName(resultSet.getString(ColumnName.USER_FIRST_NAME));
                user.setLastName(resultSet.getString(ColumnName.USER_LAST_NAME));
                user.setBalance(resultSet.getBigDecimal(ColumnName.USER_BALANCE));
                user.setStatus(UserStatus.valueOf(resultSet.getString(ColumnName.USER_STATUS)));
                user.setRole(UserRole.valueOf(resultSet.getString(ColumnName.USER_ROLE)));

                optionalUser = Optional.of(user);
            } else {
                optionalUser = Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Couldn't execute query " + SQL_SELECT_USER_BY_ID, e);
        }
        logger.log(Level.INFO, "Return user by id = " + id + ": " + optionalUser);
        return optionalUser;
    }

    @Override
    public boolean delete(Long id) throws DaoException {
        boolean isDeleteSuccessful;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_USER_BY_ID)) {

            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            isDeleteSuccessful = preparedStatement.getUpdateCount() > 0;

        } catch (SQLException e) {
            throw new DaoException("Couldn't execute query " + SQL_DELETE_USER_BY_ID, e);
        }

        logger.log(Level.INFO, "Deleted user by id: " + id + "; Delete successful:" + isDeleteSuccessful);
        return isDeleteSuccessful;
    }

    @Override
    public boolean delete(User entity) throws DaoException {
        Long userId = entity.getUserId();
        return delete(userId);
    }

    @Override
    public boolean create(User entity) throws DaoException {
        boolean isCreateSuccessful;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_USER)) {

            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFirstName());
            preparedStatement.setString(4, entity.getLastName());
            preparedStatement.setBigDecimal(5, entity.getBalance());
            preparedStatement.setString(6, entity.getStatus().name());
            preparedStatement.setString(7, entity.getRole().name());

            preparedStatement.execute();
            isCreateSuccessful = preparedStatement.getUpdateCount() > 0;

        } catch (SQLException e) {
            throw new DaoException("Couldn't execute query " + SQL_INSERT_USER, e);
        }

        logger.log(Level.INFO, "Inserted user: " + entity + "; Insert successful:" + isCreateSuccessful);
        return isCreateSuccessful;
    }

    @Override
    public User update(User entity) throws DaoException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_USER_BY_ID)) {

            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFirstName());
            preparedStatement.setString(4, entity.getLastName());
            preparedStatement.setBigDecimal(5, entity.getBalance());
            preparedStatement.setString(6, entity.getStatus().name());
            preparedStatement.setString(7, entity.getRole().name());
            preparedStatement.setLong(8, entity.getUserId());
            preparedStatement.execute();

            if (preparedStatement.getUpdateCount() == 0) {
                throw new DaoException("No rows updated");
            }

            Long updatedUserId = entity.getUserId();
            User userFromDb = findEntityById(updatedUserId).orElseThrow(() -> new DaoException("Updated user was not found by id: " + updatedUserId));
            logger.log(Level.INFO, "Updated user: " + userFromDb);
            return userFromDb;

        } catch (SQLException e) {
            throw new DaoException("Couldn't execute query " + SQL_UPDATE_USER_BY_ID, e);
        }
    }
}
