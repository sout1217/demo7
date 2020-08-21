package com.example.demo7;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;


class ControllerTests {

    @Test
    void assertTest () {

        Assert.notNull(null, "하이");
        System.out.println("바이");

    }

}