package com.levon.auction.model.connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatabasePropertiesReader {
    private static Logger logger = LogManager.getLogger();

    static final String DATABASE_URL;
    static final String DATABASE_USER;
    static final String DATABASE_PASSWORD;
    static final String DATABASE_DRIVER_NAME;
    static final int CONNECTION_POOL_SIZE;

    static {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("\\settings\\db");
            DATABASE_URL = resourceBundle.getString("db.url");
            DATABASE_USER = resourceBundle.getString("db.user");
            DATABASE_PASSWORD = resourceBundle.getString("db.password");
            DATABASE_DRIVER_NAME = resourceBundle.getString("db.driver_name");
            CONNECTION_POOL_SIZE = Integer.parseInt(resourceBundle.getString("db.pool_size"));
        } catch (MissingResourceException e) {
            logger.log(Level.FATAL, e);
            throw new ExceptionInInitializerError("MissingResourceException: " + e.getMessage());
        }
    }

    private DatabasePropertiesReader() {
    }
}
