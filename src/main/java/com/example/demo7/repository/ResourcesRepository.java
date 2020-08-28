package com.example.demo7.repository;

import com.example.demo7.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {


    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("SELECT r FROM Resources r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc")
    List<Resources> findAllResources();

    @Query("SELECT r FROM Resources r join fetch r.roleSet where r.resourceType = 'method' order by r.orderNum desc")
    List<Resources> findAllMethodResources();

    @Query("SELECT r FROM Resources r join fetch r.roleSet where r.resourceType = 'pointcut' order by r.orderNum desc")
    List<Resources> findAllPointcutResources();
}
