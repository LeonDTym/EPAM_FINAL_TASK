package com.levon.auction.model.dao;

import com.levon.auction.exception.DaoException;
import com.levon.auction.model.connection.ConnectionPool;
import com.levon.auction.model.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

//TODO interface & implementations
public abstract class AbstractDao<T extends Entity> {

    protected static final ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static final Logger logger = LogManager.getLogger();

    public abstract List<T> findAll() throws DaoException;
    public abstract Optional<T> findEntityById(Long id) throws DaoException;
    //TODO remove delete methods?
    public abstract boolean delete(Long id) throws DaoException;
    public abstract boolean delete(T entity) throws DaoException;
    public abstract boolean create(T entity) throws DaoException;
    public abstract T update(T entity) throws DaoException;

}
