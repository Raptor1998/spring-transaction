package com.raptor.transaction.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author raptor
 * @description MyTransactionManager
 * @date 2022/4/28 15:08
 */
@Component
public class MyTransactionManager {
    @Autowired
    DataSource dataSource;

    //保证在一个线程中拿到的连接时同一个连接
    ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();


    public Connection getConnection() throws SQLException {
        if (connectionThreadLocal.get() != null) {
            return connectionThreadLocal.get();
        } else {
            connectionThreadLocal.set(dataSource.getConnection());
        }
        return connectionThreadLocal.get();
    }
}
