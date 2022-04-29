package com.raptor.transaction.jdbc.annotation;

import com.raptor.transaction.jdbc.MyTransactionManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.plugin2.message.Message;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author raptor
 * @description MyTransactionAspect
 * @date 2022/4/28 16:28
 */
@Component
@Aspect
public class MyTransactionAspect {

    @Autowired
    MyTransactionManager transactionManager;


    @Around("@annotation(MyTransaction)")
    public Object doTransaction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Connection connection = transactionManager.getConnection();
        connection.setAutoCommit(false);
        System.out.println("事务开始");
        try {
            Object proceed = proceedingJoinPoint.proceed();
            connection.commit();
            System.out.println("事务提交");
            return proceed;
        } catch (Exception e) {
            int flag = 0;
            Class<?> clazz = proceedingJoinPoint.getTarget().getClass();
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method targetMethod=
                    clazz.getDeclaredMethod(
                            signature.getName(),
                            signature.getParameterTypes());
            MyTransaction annotation = targetMethod.getAnnotation(MyTransaction.class);
            if (annotation==null){
                e.printStackTrace();
                System.out.println("事务回滚");
                connection.rollback();
            }else {
                Class[] classes = annotation.rollbackFor();
                for (Class aClass : classes) {
                    if (e.getClass().getName().equals(aClass.getName())){
                        e.printStackTrace();
                        System.out.println("指定的错误，事务回滚");
                        flag++;
                    }
                }
            }
            if (flag>0){
                connection.rollback();
            }else {
                System.out.println("不是指定的异常，提交事务");
                connection.commit();
            }

        } finally {
            connection.close();
        }
        return null;
    }
}
