package com.example.demo7.security.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAccessDeniedHandler implements AccessDeniedHandler {

    // 인증은 됐으나 권한을 만족하지 못할 경우 접근 불가
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");

    }
}
