package com.quantizer.aiprogrammingassistant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;



public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey;
    private final JwtUtils jwtUtils;

    public JWTAuthenticationFilter(String secretKey, JwtUtils jwtUtils) {
        this.secretKey = secretKey;
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {
                try {
                    String authorizationHeader = request.getHeader("Authorization");
        
                    if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                    }
        
                    String token = authorizationHeader.substring(7);
        
                    if (!jwtUtils.validateToken(token, secretKey)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
        
                    String username = jwtUtils.getUsernameFromToken(token, secretKey);
                    List<GrantedAuthority> authorities = jwtUtils.getAuthoritiesFromToken(token, secretKey);
        
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    logger.error("Error authenticating user", e);
                }
        
                filterChain.doFilter(request, response);
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
}
