package com.raptor.transaction.service;

import com.raptor.transaction.jdbc.MyTransactionManager;
import com.raptor.transaction.jdbc.annotation.MyTransaction;
import com.raptor.transaction.jdbc.template.MyJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
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
    MyJdbcTemplate myJdbcTemplate;

    @Autowired
    MyTransactionManager transactionManager;

    @Override
    @MyTransaction(rollbackFor = {ArithmeticException.class})
    public void insert() throws SQLException {
        /**
         * 两条sql语句如何保证在一个连接内
         */
        myJdbcTemplate.execute("insert into user(nickname) values('" + UUID.randomUUID().toString().substring(0, 5) + "')");
        myJdbcTemplate.execute("insert into user(nickname) values('" + UUID.randomUUID().toString().substring(0, 5) + "')");
        //int a = 10 / 0;
        int b[] = new int[]{1, 32, 3};
        int c = b[10];


    }
}
