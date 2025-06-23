package com.sni.insurancecompany.insurancecompany.security;

import com.sni.insurancecompany.insurancecompany.model.enums.Role;
import com.sni.insurancecompany.insurancecompany.model.Session;
import com.sni.insurancecompany.insurancecompany.model.User;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import com.sni.insurancecompany.insurancecompany.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExp;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExp;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExp, true);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExp, false);
    }

    @Transactional
    private String createToken(Authentication authentication, long valid, boolean storeInSession) {
        String username = authentication.getName();
        String role = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse(Role.CLIENT.name());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Nije pronadjen user" +username));

        Date now = new Date();

        Date expire = new Date(now.getTime()+valid);


        String token =  Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        if(storeInSession) {
            System.out.println("Generisani token " +token);
            Session session = new Session();
            System.out.println("Vreme kreiranja sesije: " + LocalDateTime.now());
            System.out.println("Vreme u bazi (createdAt): " + session.getCreatedAt());
            session.setSessionToken(token.trim());
            session.setUser(user);
            session.setIsActive(true);
            LocalDateTime createdAt = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime expireAt = expire.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            session.setExpiredAt(expireAt);
            session.setCreatedAt(createdAt);
            sessionRepository.save(session);
        }

        return token;
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role getRole(String token) {

        String role = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(role);
    }

    public boolean validateToken(String token) {
        try {
            Optional<Session> sessionOptional = sessionRepository.findBySessionToken(token);
            if(sessionOptional.isEmpty()) {
                System.out.println("Nije pronadjena za token");
                return false;
            }
            Session session = sessionOptional.get();
            if(!session.getIsActive() ||
                    (session.getExpiredAt() != null && session.getExpiredAt().isBefore(LocalDateTime.now()))) {
                System.out.println("Session inactive or expired");
                return false;
            }

            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: " +e.getMessage());
            return false;
        }
    }

    public String refreshToken(String oldToken) {
        try {

            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(oldToken);

            String username = getUsername(oldToken);
            String role = getRole(oldToken).name();
            System.out.println(role);
            Optional<Session> sessionOptional = sessionRepository.findBySessionToken(oldToken);
            if(sessionOptional.isPresent()) {
                Session session = sessionOptional.get();
                if (!session.getIsActive() ||
                        (session.getExpiredAt() != null && session.getExpiredAt().isBefore(LocalDateTime.now()))) {
                    throw new IllegalArgumentException("Refresh token expired");
                }

                session.setIsActive(false);
                sessionRepository.save(session);
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );

            return createAccessToken(authentication);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Refresh token validation failed", e);
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

}
