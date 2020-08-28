package com.example.demo7.security.configs;

import com.example.demo7.security.factory.MethodResourceFactoryBean;
import com.example.demo7.security.processor.ProtectPointcutPostProcessor;
import com.example.demo7.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    /** reflection 방법
     *  공식적인 방법이 아니기 때문에 postProcessBeforeInitialization 에서 for 문을 돌 떄 오류가 발생한다
     *  ProtectPointcutPostProcessor 를 복사하여 클래스를 만든 후, attemptMatch 메소드 안에서 try catch 를
     *  감싸주면 for 문을 무사히 돌 수 있게 하면 해결이 되긴한다 (위의 protectPointcutPostProcessor 방법)
     * */
//    @Bean
//    BeanPostProcessor protectPointcutPostProcessor() throws Exception {
//
//        Class<?> clazz = Class.forName("org.springframework.security.config.method.ProtectPointcutPostProcessor");
//
//        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(MapBasedMethodSecurityMetadataSource.class);
//        declaredConstructor.setAccessible(true);
//        Object instance = declaredConstructor.newInstance(mapBasedMethodSecurityMetadataSource());
//        Method setPointcutMap = instance.getClass().getMethod("setPointcutMap",  Map.class);
//        setPointcutMap.setAccessible(true);
//        setPointcutMap.invoke(instance,  pointcutResourcesMapFactoryBean().getObject());
//
//        return (BeanPostProcessor) instance;
//    }
}
