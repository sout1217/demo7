package com.example.demo7.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

public class AjaxAuthenticationToken extends AbstractAuthenticationToken {

//    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // 이미 AbstractAuthenticationToken 의 구현된 클래스 UsernamePasswordAuthenticationToken 가 존재하기 때문에
    // 내용 일부를 그대로 가져다 사용한다
    private final Object principal;
    private Object credentials;

    // 익명 사용자 생성자
    public AjaxAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    // 인증된 사용자 생성자
    public AjaxAuthenticationToken(Object principal, Object credentials,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
