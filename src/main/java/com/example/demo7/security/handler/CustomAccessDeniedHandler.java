package com.example.demo7.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // 에러 페이지 주소
    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 에러 페이지 주소에 url 파라미터로 exception 메시지를 보낸다
        // 한글 출력을 위해 인코딩 함
        String deniedUrl = errorPage + "?exception=" + URLEncoder.encode(accessDeniedException.getMessage(), "UTF-8");;
        response.sendRedirect(deniedUrl);

    }

    // Security Config 에서 @Bean 으로 생성 할 때 주소를 설정하기 위해 Setter 메소드를 추가한다
    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
}
