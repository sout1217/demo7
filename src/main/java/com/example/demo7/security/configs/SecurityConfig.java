package com.example.demo7.security.configs;

import com.example.demo7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;


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
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("!/h2-console/**"))
        .and()
                .headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy", "script-src 'self'")).frameOptions().disable()
        .and()
                .authorizeRequests()
                .antMatchers("/", "/users", "/h2-console", "/h2-console/*").permitAll() // /users 회원가입 페이지 인증없이 누구나 접근하기 위함
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/message").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()
        .and()
                .formLogin()
        ;
    }
}
