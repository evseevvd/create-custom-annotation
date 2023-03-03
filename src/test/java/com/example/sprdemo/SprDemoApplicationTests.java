package com.example.sprdemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SprDemoApplicationTests {

    @Autowired
    private MyService myService;

    @Test
    void contextLoads() {
    }


    @Test
    void checkException() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> myService.method1());
    }
}
