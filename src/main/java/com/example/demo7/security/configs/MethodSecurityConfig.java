package com.example.demo7.security.configs;

import com.example.demo7.security.factory.MethodResourceFactoryBean;
import com.example.demo7.security.interceptor.CustomMethodSecurityInterceptor;
import com.example.demo7.security.processor.ProtectPointcutPostProcessor;
import com.example.demo7.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private SecurityResourceService securityResourceService;

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return mapBasedMethodSecurityMetadataSource();
    }

    @Bean
    public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {

        return new MapBasedMethodSecurityMetadataSource(methodResourcesMapFactoryBean().getObject());
    }

    @Bean
    public MethodResourceFactoryBean methodResourcesMapFactoryBean() {
        MethodResourceFactoryBean methodResourceFactoryBean = new MethodResourceFactoryBean(securityResourceService);
        methodResourceFactoryBean.setResourceType("method");
        return methodResourceFactoryBean;
    }

    @Bean
    public MethodResourceFactoryBean pointcutResourcesMapFactoryBean() {
        MethodResourceFactoryBean methodResourceFactoryBean = new MethodResourceFactoryBean(securityResourceService);
        methodResourceFactoryBean.setResourceType("pointcut");
        return methodResourceFactoryBean;
    }

    /** 이 메소드를 사용하기 위해서는 Pointcut 보안에 대한 DB 데이터가 1개 이상 존재해야한다 */
    @Bean
    public ProtectPointcutPostProcessor protectPointcutPostProcessor() {
        ProtectPointcutPostProcessor protectPointcutPostProcessor = new ProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource());
        protectPointcutPostProcessor.setPointcutMap(pointcutResourcesMapFactoryBean().getObject());
        return protectPointcutPostProcessor;
    }

    /** 이 커스텀 클래스가 Advice 이고, 인가 처리를 담당한다 */
    @Bean
    public CustomMethodSecurityInterceptor customMethodSecurityInterceptor(MapBasedMethodSecurityMetadataSource methodSecurityMetadataSource) {
        CustomMethodSecurityInterceptor customMethodSecurityInterceptor = new CustomMethodSecurityInterceptor();
        customMethodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
        customMethodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
        customMethodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource);
        RunAsManager runAsManager = runAsManager();

        if (runAsManager != null) {
            customMethodSecurityInterceptor.setRunAsManager(runAsManager);
        }

        return customMethodSecurityInterceptor;
    }
}
