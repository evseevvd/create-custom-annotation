package com.example.sprdemo;

import org.springframework.stereotype.Component;

@Component("fooExceptionHandler")
public class CustomExceptionHandlerBean implements ExceptionHandler {


    @Override
    public void handle(Throwable throwable) {
        System.out.print("Наш обработчик");
    }
}
