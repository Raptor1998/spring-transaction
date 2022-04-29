package com.raptor.transaction.jdbc.annotation;

import com.raptor.transaction.jdbc.MyTransactionManager;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * @author raptor
 * @description MyTransactionAspect
 * @date 2022/4/28 16:28
 */
@Component
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class MyTransactionAspect {

    @Autowired
    MyTransactionManager transactionManager;


    ThreadLocal<Boolean> hasTransaction = new ThreadLocal();


    //@Before("@annotation(com.raptor.transaction.jdbc.annotation.MyTransaction)")
    //public void te() {
    //    System.out.println("执行前");
    //}

    @Around("@annotation(MyTransaction)")
    public Object doTransaction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Connection connection = transactionManager.getConnection();
        Class<?> clazz = proceedingJoinPoint.getTarget().getClass();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method targetMethod = clazz.getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        MyTransaction annotation = targetMethod.getAnnotation(MyTransaction.class);
        connection.setAutoCommit(false);
        System.out.println("事务开始");
        try {
            Object proceed = proceedingJoinPoint.proceed();
            connection.commit();
            System.out.println("事务提交");
            return proceed;
        } catch (Exception e) {
            rollback(connection, e, annotation);
        } finally {
            connection.close();
        }
        return null;
    }

    public void rollback(Connection connection, Exception e, MyTransaction annotation) throws SQLException, NoSuchMethodException {
        int flag = 0;
        Class[] classes = annotation.rollbackFor();
        for (Class aClass : classes) {
            if (e.getClass().getName().equals(aClass.getName())) {
                e.printStackTrace();
                System.out.println("指定的错误，事务回滚");
                flag++;
            }
        }
        if (flag > 0) {
            System.out.println("事务回滚");
            connection.rollback();
        } else if (flag == 0 && !classes[0].getName().equals(Exception.class.getName())) {
            System.out.println("不是指定的异常，提交事务");
            connection.commit();
        } else {
            System.out.println("未标注指定异常");
            System.out.println("事务回滚");
            connection.rollback();
        }

    }
}
