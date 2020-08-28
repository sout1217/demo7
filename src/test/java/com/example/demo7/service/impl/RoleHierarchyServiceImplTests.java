package com.example.demo7.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RoleHierarchyServiceImplTests {


    @Autowired
    private RoleHierarchyServiceImpl roleHierarchyService;


    @Test
    void findAllHierarchy() {

        String allHierarchy = roleHierarchyService.findAllHierarchy();
        System.out.println();
        System.out.println(allHierarchy);

    }
}