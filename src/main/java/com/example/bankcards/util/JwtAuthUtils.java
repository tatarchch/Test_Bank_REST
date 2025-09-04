package com.example.bankcards.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@UtilityClass
@Slf4j
public class JwtAuthUtils {

    private final int jwtExpirationMs = 36 * (int) Math.pow(10, 5);

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Jwts.SIG.HS512.key().build().getEncoded());

    public String generateJwtToken(Authentication authentication) {

        String username = authentication.getPrincipal().toString();

        List<String> role = authentication.getAuthorities().stream()
                .filter(SimpleGrantedAuthority.class::isInstance)
                .map(SimpleGrantedAuthority.class::cast)
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims validateJwtToken(String authToken) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    public List<SimpleGrantedAuthority> castJwtAuthority(Claims claims) {
        List<String> roles = claims.get("role", List.class);

        List<SimpleGrantedAuthority> simpleRoles = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new ArrayList<>(simpleRoles);
    }

}
