package com.example.waaaugust2022security.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Aspect
@Component
public class WaaOffensiveWords {

    public static int badWords = 0;
    public String BAD_WORD = "spring";

    @Pointcut("within(com.example.waaaugust2022security.service.impl.ProductServiceImpl)")
    public void getPointCut() {
    }

    @Before("getPointCut()")
    public void checkOffensiveWords(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream().
                collect(Collectors.toMap(h -> h, request::getHeader));

        String username = request.getUserPrincipal().getName();

        if(BanUser.timeToWait()!=0) {
            System.out.println(String.format("Max Bad Words Requests Limit has been Reached. You need wait for %s minutes.", BanUser.timeToWait()));
        }

        if (request.getMethod().equalsIgnoreCase("POST")){
            AtomicBoolean isBadWord = new AtomicBoolean(false);

            headers.forEach((key, val) -> {
                if ((BAD_WORD.equalsIgnoreCase(val) || val.contains(BAD_WORD)))
                    isBadWord.set(true);
            });

            if(!isBadWord.get()){
                badWords++;
            }
        }
    }
}
