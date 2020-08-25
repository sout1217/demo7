package com.example.demo7.security.provider;

import com.example.demo7.security.common.FormWebAuthenticationDetails;
import com.example.demo7.security.service.AccountContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/** DB 에서 Username 으로 찾은 DB 계정에 대한 검증을 담당하는 클래스 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

    // DB 에서 Account 를 받기 위해 의존성 주입한다
    @Autowired
    private UserDetailsService userDetailsService;

    // 패스워드 일치 검사를 위해 의존성 주입한다
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // 매개변수에 있는 authentication 에는 form 에 입력한 id 와 password 가 담겨져 있다

        // authentication 객체에서 name 을 가져온다
        String username = authentication.getName();
        // authentication 객체에서 password 를 가져온다
        String password = (String) authentication.getCredentials(); // return Object

        // UserDetail = User = UserContext = ~~~UserRepository.loadUserByUsername(username) = DB 계정
        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);

        // passwordEncoder.matches(폼에 입력된 password 와 DB 에 입력된 password 가 일치하는 지 검사
        if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            // 일치하지 않는 경우 exception 발생
            throw new BadCredentialsException("BadCredentialsException");
        }

        // Authentication 안의 details 는 Object 이다 (cascading 가능)
        // details 에는 기본적으로 Spring Security 가 처리한 remoteAddress 와 sessionId 에 정보도 담긴다
        FormWebAuthenticationDetails formWebAuthenticationDetails = (FormWebAuthenticationDetails) authentication.getDetails();
        String secretKey = formWebAuthenticationDetails.getSecretKey();
        if (secretKey == null || !"secret".equals(secretKey)) {
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
        }

        // UsernamePasswordAuthenticationToken 생성 (DB 에서 가져온 account 와, 비밀번호는 null,  DB 에서 가져온 Role 으로 생성함 ) - account 에는 username, email, age 등 포함되어있다.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());

        // 결국 Object Authentication 은 Account, null, roles 가 된다.
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 1. ProviderManager 에서 providers 리스트를 for 문으로 돌면서 supports 를 체크한다
        // 2. 인자로 넘어온 authentication 이 UsernamePasswordAuthenticationToken 과 일치할 때 검증을 처리한다
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
