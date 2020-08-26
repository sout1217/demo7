package com.example.demo7.service;

import com.example.demo7.domain.entity.Resources;
import com.example.demo7.repository.ResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class SecurityResourceService {

    @Autowired
    private ResourcesRepository resourcesRepository;

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();

        List<Resources> resourcesList = resourcesRepository.findAllResources();

        resourcesList.forEach(resources -> {

            List<ConfigAttribute> configAttributes = new ArrayList<>();

            resources.getRoleSet().forEach(role -> {

                configAttributes.add(new SecurityConfig(role.getRoleName()));

                result.put(new AntPathRequestMatcher(resources.getResourceName()), configAttributes);

            });
        });

        return result;
    }
}
