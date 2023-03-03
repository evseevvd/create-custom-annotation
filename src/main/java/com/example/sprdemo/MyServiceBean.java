package com.example.sprdemo;

import org.springframework.stereotype.Service;

@Service
public class MyServiceBean implements MyService {

    @MyExceptionHandler(handler = "fooExceptionHandler")
    @Override
    public String method1() {
        throw new IllegalArgumentException();
    }

    @Override
    public String method2() {
        return "Привет";
    }
}
