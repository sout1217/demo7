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

            });
            result.put(new AntPathRequestMatcher(resources.getResourceName()), configAttributes);
            // (이전 글에서 잘못 작성되었다) Resource 하나 당 여러개의 Role 들어가야 하기 때문에 이 구문은 밖으로 빼주어야 한다
        });

        return result;
    }

    public List<String> getAccessIpList() {
        return accessIpRepository.findAll().stream()
                .map(AccessIp::getIpAddress)
                .collect(Collectors.toList());
    }

    public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList() {

        LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();

        List<Resources> resourcesList = resourcesRepository.findAllMethodResources();

        resourcesList.forEach(resources -> {

            List<ConfigAttribute> configAttributes = new ArrayList<>();

            resources.getRoleSet().forEach(role -> {

                configAttributes.add(new SecurityConfig(role.getRoleName()));

            });
            result.put(resources.getResourceName(), configAttributes);
        });

        return result;
    }
}
