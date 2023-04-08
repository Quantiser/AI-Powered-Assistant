package com.quantizer.aiprogrammingassistant.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        claims.put("authorities", authorities);
        return doGenerateToken(claims, authentication.getName());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public boolean validateToken(String token, String secretKey) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token, String secretKey) {
        return getClaimFromToken(token, Claims::getSubject, secretKey);
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token, String secretKey) {
        List<String> authorities = getClaimFromToken(token, claims -> (List<String>) claims.get("authorities"), secretKey);
        return authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = getAllClaimsFromToken(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }


}
