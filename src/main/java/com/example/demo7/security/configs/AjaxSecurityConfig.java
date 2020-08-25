package com.example.demo7.security.configs;

import com.example.demo7.security.common.AjaxLoginAuthenticationEntryPoint;
import com.example.demo7.security.handler.AjaxAccessDeniedHandler;
import com.example.demo7.security.handler.AjaxAuthenticationFailureHandler;
import com.example.demo7.security.handler.AjaxAuthenticationSuccessHandler;
import com.example.demo7.security.provider.AjaxAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@Order(0) // 첫번째로 진행되는 시큐리티
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 인증 Provider 를 ajaxAuthenticationProvider 로 정의
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        // 인증 provider Bean 생성
        return new AjaxAuthenticationProvider();
    }

    @Bean
    public AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        // 커스텀 SuccessHandler Bean 생성
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        // 커스텀 FailureHandler Bean 생성
        return new AjaxAuthenticationFailureHandler();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/messages").hasRole("MANAGER")
                .antMatchers("/api/login").permitAll() // 추가됨
                .anyRequest().authenticated()
        ;

        http
                .exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint()) // 인증을 받지 않은 사용자가 자원에 접근 했을 경우
                .accessDeniedHandler(ajaxAccessDeniedHandler()) // 인증을 받았지만 권한을 만족하지 못하는 경우
        ;

//        http
//                .csrf().disable()
//        ;

        customconfigurerAjax(http);
    }

    private void customconfigurerAjax(HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>())
                .successHandlerAjax(ajaxAuthenticationSuccessHandler())
                .failureHandlerAjax(ajaxAuthenticationFailureHandler())
                .setAuthenticationManager(authenticationManagerBean())
                .loginProcessingUrl("/api/login")
        ;
    }

    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
