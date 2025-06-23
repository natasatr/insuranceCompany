package com.sni.insurancecompany.insurancecompany.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sni.insurancecompany.insurancecompany.model.Session;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import com.sni.insurancecompany.insurancecompany.services.SIEMService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class MaliciousRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(MaliciousRequestFilter.class);
    @Autowired
    private SIEMService siemService;
    @Autowired
    private SessionRepository sessionRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/payment/process")) {
            String amount = null;
            String requestURI = request.getRequestURI();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> body = objectMapper.readValue(request.getInputStream(), Map.class);

                if (body.containsKey("amount")) {
                    Object amountValue = body.get("amount");
                    amount = amountValue.toString();
                }
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }

            if (isPotentiallyMalicious(amount)) {
                String warningMessage = String.format("Detektovan potencijalno maliciozni zahtjev: URI=%s Amount=%s", requestURI, amount);
                siemService.logSecurityEvent("Maliciozni zahtjev: ", warningMessage);

                String sessionToken = null;
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("token".equals(cookie.getName())) {
                            sessionToken = cookie.getValue();
                            break;
                        }
                    }
                }

                if (sessionToken != null) {
                    Optional<Session> sessionOptional = sessionRepository.findBySessionToken(sessionToken);
                    if (sessionOptional.isPresent()) {
                        Session session = sessionOptional.get();
                        session.setIsActive(false);
                        sessionRepository.save(session);
                    }
                }

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Maliciozni zahtjev detektovan.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPotentiallyMalicious(String amount) {
        try {
            if (amount != null) {
                double value = Double.parseDouble(amount);
                return value > 10_000;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}
