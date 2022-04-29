package com.raptor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * @author raptor
 * @description UserServiceImpl
 * @date 2022/4/28 9:53
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void insert() throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        try {
            String s = UUID.randomUUID().toString().substring(0, 5);

            //配合spring事务
            //jdbcTemplate.execute("insert into user(nickname) values('" + s + "')");

            //手动控制
            statement.execute("insert into user(nickname) values('" + s + "')");
            //int a = 12 / 0;

            connection.commit();
        } catch (Exception e) {
            System.out.println("异常"+e.getMessage());
            connection.rollback();
        }finally {
            //关闭是放回连接池还是关闭java和mysql的连接
            //不一定，连接池不同，可能不同，一般是放回连接池
            connection.close();
        }
    }
}
