package com.sni.insurancecompany.insurancecompany.controllers;

import com.sni.insurancecompany.insurancecompany.dto.JwtResponse;
import com.sni.insurancecompany.insurancecompany.dto.LoginRequest;
import com.sni.insurancecompany.insurancecompany.dto.RefreshTokenRequest;
import com.sni.insurancecompany.insurancecompany.dto.RegisterUserRequest;
import com.sni.insurancecompany.insurancecompany.model.enums.Role;
import com.sni.insurancecompany.insurancecompany.model.Session;
import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.model.VerificationEmailCode;
import com.sni.insurancecompany.insurancecompany.model.enums.UserStatus;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import com.sni.insurancecompany.insurancecompany.repositories.UserRepository;
import com.sni.insurancecompany.insurancecompany.repositories.VerificationEmailCodeRepository;
import com.sni.insurancecompany.insurancecompany.security.JwtTokenProvider;
import com.sni.insurancecompany.insurancecompany.services.EmailService;
import com.sni.insurancecompany.insurancecompany.services.UserService;
import com.sni.insurancecompany.insurancecompany.services.SIEMService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccessController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SIEMService siemService;
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final VerificationEmailCodeRepository verificationEmailCodeRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getStatus() == UserStatus.BLOCK) {
                    siemService.logSecurityEvent("Blocked user login attempt",
                            "Blocked user tried to login: " + loginRequest.getUsername());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Your account is blocked. Please contact administrator.");
                }
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            userOptional = userRepository.findByUsername(username);
            if(userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found "+username);
            }

            User user = userOptional.get();


            if(Role.CLIENT.name().equalsIgnoreCase(user.getRole().name())) {
                user.setFailed2FAAttempts(0);
                userRepository.save(user);

                String code = userService.generateVerificationCode();
                VerificationEmailCode verificationEmailCode = new VerificationEmailCode(
                        code, username, LocalDateTime.now().plusMinutes(20)
                );
                verificationEmailCodeRepository.save(verificationEmailCode);
                emailService.sendVerificationCode(user.getEmail(), code);

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "2FA code sent to your email");
                responseBody.put("username", username);
                return ResponseEntity.ok(responseBody);
            }

            String access = jwtTokenProvider.createAccessToken(authentication);
            String refresh = jwtTokenProvider.createRefreshToken(authentication);
            setCookies(response, access, refresh);

            siemService.logSecurityEvent("Successful login", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(access, refresh));

        } catch (BadCredentialsException e) {

            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);


                if (user.getFailedLoginAttempts() >= 5) {
                    user.setStatus(UserStatus.BLOCK);
                    siemService.logSecurityEvent("User blocked",
                            "User blocked due to multiple failed login attempts: " + loginRequest.getUsername());
                }
                userRepository.save(user);
            }

            siemService.logSecurityEvent("Failed login attempt", "Invalid credentials for: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            siemService.logSecurityEvent("Login error", "Error during login for: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login error occurred");
        }
    }

    @PostMapping("/register/employee")
    public ResponseEntity<String> registerEmployee(@RequestBody RegisterUserRequest registerRequest, HttpServletResponse response) {
        try {
            clearAuthCookies(response);
            userService.registerEmployee(registerRequest);
            siemService.logSecurityEvent("Employee registered", "Employee registered: " + registerRequest.getUsername());
            return ResponseEntity.ok("Employee successfully registered: " + registerRequest.getUsername());
        } catch (IllegalArgumentException e) {
            siemService.logSecurityEvent("Failed employee registration", "Error registering employee: " + registerRequest.getUsername());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/client")
    public ResponseEntity<String> registerClient(@RequestBody RegisterUserRequest registerUserRequest, HttpServletResponse response) {
        try {
            clearAuthCookies(response);
            userService.registerClient(registerUserRequest);
            siemService.logSecurityEvent("Client registered", "client registered with username " +registerUserRequest.getUsername());
            return ResponseEntity.ok("Client successfully registered"+ registerUserRequest.getUsername());
        }catch(IllegalArgumentException e) {
            siemService.logSecurityEvent("Failed client registration", "Error registring client: " + registerUserRequest.getUsername());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
   @PostMapping("/verify-2fa")
   @Transactional
   public ResponseEntity<?> verify2FA(@RequestBody Map<String, String> request, HttpServletResponse response) {
       String username = request.get("username");
       String code = request.get("code");
       if (username == null || code == null) {
           return ResponseEntity.badRequest().body("Username and code are required");
       }

       try {
           User user = userRepository.findByUsername(username)
                   .orElseThrow(() -> new RuntimeException("User not found"));

           if (user.getStatus() == UserStatus.BLOCK) {
               siemService.logSecurityEvent("Blocked user attempt", "Blocked user tried to verify 2FA: " + username);
               return ResponseEntity.status(HttpStatus.FORBIDDEN)
                       .body("Your account is blocked. Please contact administrator.");
           }

           VerificationEmailCode verificationCode = verificationEmailCodeRepository
                   .findByCodeAndUsernameAndUsedFalseAndExpiredAtAfter(code, username, LocalDateTime.now())
                   .orElseThrow(() -> {
                       user.setFailed2FAAttempts(user.getFailed2FAAttempts() + 1);

                       if (user.getFailed2FAAttempts() >= 2) {
                           user.setStatus(UserStatus.BLOCK);
                           siemService.logSecurityEvent("User blocked",
                                   "User blocked due to 2 failed 2FA attempts: " + username);
                       }

                       userRepository.save(user);
                       return new RuntimeException("Invalid or expired verification code");
                   });

           user.setFailed2FAAttempts(0);
           userRepository.save(user);

           verificationCode.setUsed(true);
           VerificationEmailCode savedCode = verificationEmailCodeRepository.save(verificationCode);

           if (savedCode == null) {
               throw new RuntimeException("Failed to update verification code");
           }

           Authentication authentication = new UsernamePasswordAuthenticationToken(
                   username,
                   null,
                   Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())
                   ));
           String accessToken = jwtTokenProvider.createAccessToken(authentication);
           String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

           setCookies(response, accessToken, refreshToken);
           siemService.logSecurityEvent("Successful 2fa login", "user logged in with 2fa: " + username);
           return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));

       } catch (RuntimeException e) {
           siemService.logSecurityEvent("Failed 2fa", "Error: " + e.getMessage());
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
       }
   }
    private void setCookies(HttpServletResponse response, String accessToken, String refreshToken) {

        Cookie accessCookie = new Cookie("token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setDomain("localhost");
        accessCookie.setMaxAge(3600);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setDomain("localhost");
        refreshCookie.setMaxAge(7*24*60*60);
        response.addCookie(refreshCookie);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request,
                                          HttpServletRequest httpRequest,
                                          HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookies(httpRequest);

            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new IllegalArgumentException("Refresh token not provided");
            }
            String newAccessToken = jwtTokenProvider.refreshToken(refreshToken);
            setAccessTokenCookie(response, newAccessToken);
            siemService.logSecurityEvent("Token Refreshed",
                    "Access token refreshed for user: " + jwtTokenProvider.getUsername(refreshToken));
            return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken));
        } catch (Exception e) {
            siemService.logSecurityEvent("Failed Token Refresh", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie accessCookie = new Cookie("token", token);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setDomain("localhost");
        accessCookie.setMaxAge(3600);
        response.addCookie(accessCookie);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (accessToken != null) {
            Optional<Session> sessionOptional = sessionRepository.findBySessionToken(accessToken);
            if (sessionOptional.isPresent()) {
                Session session = sessionOptional.get();
                session.setIsActive(false);
                sessionRepository.save(session);
            }
        }

        Cookie accessCookie = new Cookie("token", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setDomain("localhost");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setDomain("localhost");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        siemService.logSecurityEvent("Logout", "User logged out");
        return ResponseEntity.ok("Logged out successfully");
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accCookie = new Cookie("token", null);
        accCookie.setMaxAge(0);
        accCookie.setPath("/");
        response.addCookie(accCookie);
        Cookie refCookie = new Cookie("refresh_token", null);
        refCookie.setMaxAge(0);
        refCookie.setPath("/");
        response.addCookie(refCookie);


    }

   @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuthStatus(HttpServletRequest request, HttpServletResponse response) {
        boolean isAuthenticated = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
        isAuthenticated = true;
    }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    if (jwtTokenProvider.validateToken(refreshToken)) {
                        String username = jwtTokenProvider.getUsername(refreshToken);
                        String newAccessToken = jwtTokenProvider.refreshToken(refreshToken);
                        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                        accessCookie.setHttpOnly(true);
                        accessCookie.setSecure(true);
                        accessCookie.setPath("/");
                        accessCookie.setMaxAge(15 * 60);
                        response.addCookie(accessCookie);

                        isAuthenticated = true;

                    }
                    break;
                }
            }
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("authenticated", isAuthenticated);
        return ResponseEntity.ok(result);

    }

}
