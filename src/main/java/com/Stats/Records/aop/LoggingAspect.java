package com.Stats.Records.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    public static final Logger logger= LoggerFactory.getLogger(LoggingAspect.class);

    //Learn levels in log like info,warn.. etc.
    //return type('*'- wildcard), class-name.method-name(args)
    //advice -
    @Before("execution(* com.Stats.Records.controller.PlayerController.findPl(..))")
    public void logMethodCall()
    {
        logger.info("Method-Called");
    }
}
