package com.example.demo7.repository;

import com.example.demo7.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {


    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);
}
