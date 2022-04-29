package com.raptor.transaction.jdbc.template;

import com.raptor.transaction.jdbc.MyTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author raptor
 * @description MyJdbcTemplate
 * @date 2022/4/28 14:53
 */
@Component
public class MyJdbcTemplate {

    @Autowired
    MyTransactionManager myTransactionManager;

    public void execute(String sql) throws SQLException {
        Connection connection = myTransactionManager.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }
}
