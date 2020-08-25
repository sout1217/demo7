package com.example.demo7.security.filter;

import com.example.demo7.domain.dto.AccountDto;
import com.example.demo7.security.token.AjaxAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        // 사용자 이 url 로 요청을 했을 때만 작동한다
        super(new AntPathRequestMatcher("/api/login"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        // ajax 가 아닐 때
        if (!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        // 요청으로 넘어온 정보를 AccountDto 에 담는다
        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);

        // 만약 요청으로 넘어온 정보에서 아이디 또는 패스워드가 없는 경우 exception 을 발생 시킨다
        if (StringUtils.isEmpty(accountDto.getUsername()) || StringUtils.isEmpty(accountDto.getPassword())) {
            throw new IllegalArgumentException("Username or Password is empty");
        }

        // 인증 객체 생성
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());

        // AjaxLoginProcessingFilter 에서 Manager 를 이용해 authentication 인증을 진행한다
        return this.getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        // 사용자가 요청할 때 header 에 어떤 정보를 담아서 보내서 그 정보와 같은지 안 같은지 판단
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

    }
}
