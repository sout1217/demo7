package com.example.demo7.security.provider;

import com.example.demo7.security.service.AccountContext;
import com.example.demo7.security.token.AjaxAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AjaxAuthenticationProvider implements AuthenticationProvider {

    // DB 조회용
    @Autowired
    private UserDetailsService userDetailsService;

    // 패스워드 일치 여부 확인용
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // ajax 로 보낸 username 과 password
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // username 으로 DB 조회
        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);

        // password 일치 여부
        if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        // 토큰 반환
        return new AjaxAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 넘어온 인자가 AjaxAuthenticationToken 인지 검사
        return AjaxAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
