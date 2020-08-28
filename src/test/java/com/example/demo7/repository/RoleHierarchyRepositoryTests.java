package com.example.demo7.repository;

import com.example.demo7.domain.entity.RoleHierarchy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleHierarchyRepositoryTests {

    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;

    @Test
    @Transactional
    void test() {
        RoleHierarchy roleGold = RoleHierarchy.builder()
                .childName("ROLE_GOLD")
                .build();

        RoleHierarchy roleSilver = RoleHierarchy.builder()
                .childName("ROLE_SILVER")
                .build();

        roleSilver.setParentName(roleGold);

        roleHierarchyRepository.save(roleGold);
        RoleHierarchy roleHierarchy = roleHierarchyRepository.save(roleSilver);

        System.err.println(roleHierarchy.getParentName().getChildName());

//        roleHierarchyRepository.findById(1L).ifPresent(roleHierarchy -> System.err.println(roleHierarchy.getParentName()));
//        roleHierarchyRepository.findById(2L).ifPresent(roleHierarchy -> System.err.println(roleHierarchy.getParentName()));
    }
}