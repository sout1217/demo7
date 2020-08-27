package com.example.demo7.service;

import com.example.demo7.domain.entity.AccessIp;
import com.example.demo7.domain.entity.Resources;
import com.example.demo7.repository.AccessIpRepository;
import com.example.demo7.repository.ResourcesRepository;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;

    private final AccessIpRepository accessIpRepository;

    // @Bean 설정해주지 않아도, @Repository 가 @Service 생성자에 자동주입
    public SecurityResourceService(ResourcesRepository resourcesRepository, AccessIpRepository accessIpRepository) {
        this.resourcesRepository = resourcesRepository;
        this.accessIpRepository = accessIpRepository;
    }

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

    public List<String> getAccessIpList() {
        return accessIpRepository.findAll().stream()
                .map(AccessIp::getIpAddress)
                .collect(Collectors.toList());
    }
}
