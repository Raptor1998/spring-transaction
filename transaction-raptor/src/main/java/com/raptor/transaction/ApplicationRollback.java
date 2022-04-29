package com.raptor.transaction;

import com.raptor.transaction.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;

/**
 * @author raptor
 * @description Main
 * @date 2022/4/28 9:58
 */
public class ApplicationRollback {


    public static void main(String[] args) throws SQLException {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("spring.xml");
        UserService userService = ioc.getBean(UserService.class);
        userService.insert();
    }
}
