package com.example.demo7.security.configs;

import com.example.demo7.security.common.FormAuthenticationDetailsSource;
import com.example.demo7.security.factory.UrlResourcesMapFactoryBean;
import com.example.demo7.security.filter.PermitAllFilter;
import com.example.demo7.security.handler.CustomAccessDeniedHandler;
import com.example.demo7.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.example.demo7.security.provider.CustomAuthenticationProvider;
import com.example.demo7.security.voter.IpAddressVoter;
import com.example.demo7.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FormAuthenticationDetailsSource formAuthenticationDetailsSource;

    @Autowired // TIP AuthenticationSuccessHandler 인터페이스 구현체가 2개의 @Component 를 가지고 있다면 @Qualifier("customAuthenticationSuccessHandler") 사용해야한다
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private SecurityResourceService securityResourceService;

    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

    @Override
    public void configure(WebSecurity web) throws Exception {
        // [선택 1] static 리소스 보안 적용 X (permitAll() 과 다른 점은 보안필터 접근 하기 전에 처리한다)
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

        // [선택 2]
//        web.ignoring().antMatchers("/css/**");
//        web.ignoring().antMatchers("/images/**");
//        web.ignoring().antMatchers("/js/**");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 이전에서는 저장소를 바꾸는 거 였다면
        // 여기서는 검증을 바꾼다고 생각하면 된다
        // 검증 - 저장소 임으로 저장소는 검증에 포함된다
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .requireCsrfProtectionMatcher(new AntPathRequestMatcher("!/h2-console/**"))
        .and()
                .headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'")).frameOptions().disable()
        .and()
                .authorizeRequests()
                .anyRequest().authenticated()
        .and()
                .formLogin()
                    .loginPage("/login") // 로그인 페이지 URL
                    .loginProcessingUrl("/login_proc") // action POST URL
                    .authenticationDetailsSource(formAuthenticationDetailsSource) // details 처리 클래스
                    .defaultSuccessUrl("/") // 로그인 성공 시 URL
                    .successHandler(customAuthenticationSuccessHandler) // 커스텀 핸들러
                    .failureHandler(customAuthenticationFailureHandler) // 커스텀 핸들러
                    .permitAll() // login 페이지에 대해서는 모든 사용자가 접근 가능
        .and()
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")) // 추가
                .accessDeniedPage("/denied") // 추가
                .accessDeniedHandler(accessDeniedHandler())
        .and()
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    public AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecistionVoters());
        return affirmativeBased;
    }

    public List<AccessDecisionVoter<?>> getAccessDecistionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService)); // 순서 중요
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;

    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }


    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }


    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {

        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);

        return urlResourcesMapFactoryBean;
    }

}

