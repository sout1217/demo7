package com.example.demo7.security.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // 기본 에러 메시지
        String errorMessage = "Invalid Username or Password";

        // throw 로 넘어온 exception BadCredentialsException = 비밀번호가 틀린 경우 일 때
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid Username or Password";

        // throw 로 넘어온 exception 이 InsufficientAuthenticationException = input 태그의 secret 파라미터 value 가 secret 이 아닐 때
        } else if (exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid Secret Key";
        }

        // 기본 인증 실패 url 를 아래와 같이 보낸다 ( @Controller 단 에서 처리 해주어야 한다 )
        setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }
}
