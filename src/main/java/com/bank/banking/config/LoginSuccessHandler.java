package com.bank.banking.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
        public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        var role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_MANAGER")) {
            response.sendRedirect("/manager/dashboard");
        } else {
            response.sendRedirect("/official/dashboard");
        }
    }
}

