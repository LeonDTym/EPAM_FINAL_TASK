package com.levon.auction.model.connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final Logger logger = LogManager.getLogger();

    static {
        String driverName = DatabasePropertiesReader.DATABASE_DRIVER_NAME;
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            logger.log(Level.FATAL, "Cannot get Database Driver for name" + driverName, e);
            throw new ExceptionInInitializerError("Cannot get Database Driver for name " + driverName);
        }
    }

    private ConnectionFactory() {

    }

    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DatabasePropertiesReader.DATABASE_URL, DatabasePropertiesReader.DATABASE_USER, DatabasePropertiesReader.DATABASE_PASSWORD);
    }

}
