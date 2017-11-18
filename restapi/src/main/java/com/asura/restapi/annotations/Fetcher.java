package com.asura.restapi.annotations;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Created by Mario on 2017/11/13 0013.
 * Fetcher 注解
 */
@Service
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface Fetcher {
    String code() default "";
}
