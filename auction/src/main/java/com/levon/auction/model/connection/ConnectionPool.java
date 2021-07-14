package com.levon.auction.model.connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.levon.auction.model.connection.DatabasePropertiesReader.CONNECTION_POOL_SIZE;

public class ConnectionPool {

    private static final Logger logger = LogManager.getLogger();
    private static final AtomicBoolean isInstanceCreated = new AtomicBoolean(false);
    private static ConnectionPool instance;
    private BlockingDeque<Connection> availableConnections;
    private BlockingDeque<Connection> occupiedConnections;

    private ConnectionPool() {
        availableConnections = new LinkedBlockingDeque<>();
        occupiedConnections = new LinkedBlockingDeque<>();

        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            try {
                Connection connection = ConnectionFactory.createConnection();
                ProxyConnection proxy = new ProxyConnection(connection);
                availableConnections.put(proxy);
            } catch (SQLException e) {
                logger.log(Level.ERROR, "Connection creation error", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //TODO availableConnections size check with log
    }

    public static ConnectionPool getInstance() {
        while (instance == null) {
            if (isInstanceCreated.compareAndSet(false, true)) {
                instance = new ConnectionPool();
            }
        }
        return instance;
    }

    //TODO rewrite?
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = availableConnections.take();
            occupiedConnections.put(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (!(connection instanceof ProxyConnection)) {
            logger.log(Level.WARN, "connection type is not ProxyConnection. " + connection);
            return;
        }
        occupiedConnections.remove(connection);
        try {
            availableConnections.put(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void destroyPool() {
        //TODO pool size
        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            try {
                availableConnections.take().close();
                //TODO exception handling
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        deregisterDrivers();
    }

    private void deregisterDrivers() {
        DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                //TODO exception handling
                e.printStackTrace();
            }
        });
    }

}
