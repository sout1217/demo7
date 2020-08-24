package com.example.demo7.controller.login;

import com.example.demo7.domain.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception, Model model) {

        // CustomAuthenticationFailureHandler 로 인해 "/login?error=true&exception=" + errorMessage 오게 된 경우에는
        // error 의 내용과 exception 의 내용을 model 에 담아 view 단에 보낸다
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login/login";
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(req, res, authentication);
        }

        return "redirect:/login";
    }

    @GetMapping("/denied")
    public String accessDenied(@RequestParam(value = "exception", required = false) String exception, Model model) {

        // 권한을 만족하지 못했을 뿐 인증은 되었기 때문에 SecurityContext 에서 인증 객체를 가져 올 수 있다
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // CustomAuthenticationProvider 에서 UsernamePasswordAuthenticationToken(=authentication) 을 생성했을 때
        // Object Principal 을 account 로 설정했기 때문에
        // Account 클래스로 다운 캐스케이딩이 가능하다
        Account account = (Account) authentication.getPrincipal();

        // username 과 CustomAccessDeniedHandler 에서 넘어온 exception 을 model 에 담는다
        model.addAttribute("username", account.getUsername());
        model.addAttribute("exception", exception);

        return "user/login/denied";

    }
}
