package com.example.demo7.security.factory;

import com.example.demo7.service.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodResourceFactoryBean implements FactoryBean<LinkedHashMap<String, List<ConfigAttribute>>> {


    private SecurityResourceService securityResourceService;
    private LinkedHashMap<String, List<ConfigAttribute>> resourceMap;
    private String resourceType;

    public MethodResourceFactoryBean(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public LinkedHashMap<String, List<ConfigAttribute>> getObject() {
        if (resourceMap == null) {
            init();
        }

        return resourceMap;
    }

    private void init() {

        if (resourceType.equals("method")) {
                resourceMap = securityResourceService.getMethodResourceList();
        } else if (resourceType.equals("pointcut")) {
                resourceMap = securityResourceService.getPointcutResourceList();
        }

//        switch (resourceType) {
//            case "method":
//                resourceMap = securityResourceService.getMethodResourceList();
//                break;
//            case "pointcut":
//                resourceMap = securityResourceService.getPointcutResourceList();
//
//                break;
//        }


    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
