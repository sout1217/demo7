package com.example.demo7.security.common;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String secretKey;


    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request); // WebAuthenticationDetails 에서 remoteAddress 와 sessionId 처리

        secretKey = request.getParameter("secret_key"); // 추가적인 정보를 여기서 처리
    }

    public String getSecretKey() {
        return secretKey;
    }
}
