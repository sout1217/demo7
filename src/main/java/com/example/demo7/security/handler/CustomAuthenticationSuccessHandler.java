package com.example.demo7.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 로그인 성공 시 작업 처리 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 인증 실패 시 담기는 request 캐시를 인증 후 request 캐시에 담겨져있는 내용을 여기서 처리하도록 한다

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 상위 클래스의 메소드 (기본 url 를 설정 할 수 있다) <> getDefaultTargetUrl
        setDefaultTargetUrl("/");

        // 저장되있던 캐시를 가져온다
        SavedRequest savedRequest = requestCache.getRequest(request, response);


        // 이전에 정보가 없는 경우가 있을 수 있다 ("/login" 페이지를 바로 요청한 경우)
        if (savedRequest != null) {
            // 인증 실패 이전에 가고자 했던 url 정보를 가져온다
            String targetUrl = savedRequest.getRedirectUrl();

            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }

    }
}
