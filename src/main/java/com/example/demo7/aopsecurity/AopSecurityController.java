package com.example.demo7.aopsecurity;

import com.example.demo7.domain.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Slf4j
public class AopSecurityController {

    @GetMapping("/preAuthorize")
    @PreAuthorize("hasRole('ROLE_USER') and #account.username == principal.username ")
    public String preAuthorize(AccountDto account, Model model, Principal principal) {

        log.info("account -> {}", account);
        log.info("principal -> {}", principal);

        model.addAttribute("method",  "Success @PreAuthorize");

        return "aop/method";
    }
}
