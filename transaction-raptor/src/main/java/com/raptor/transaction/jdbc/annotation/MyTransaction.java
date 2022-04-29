package com.raptor.transaction.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author raptor
 * @description MyTransaction
 * @date 2022/4/28 14:58
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTransaction {
    Class[] rollbackFor() default {Exception.class};
}
