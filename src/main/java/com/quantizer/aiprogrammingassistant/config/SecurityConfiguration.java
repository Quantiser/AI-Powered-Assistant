package com.quantizer.aiprogrammingassistant.config;

import com.quantizer.aiprogrammingassistant.config.JwtUtils;
import com.quantizer.aiprogrammingassistant.config.JWTAuthenticationFilter;
import com.quantizer.aiprogrammingassistant.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
protected void configure(HttpSecurity http) throws Exception {
    http.headers().frameOptions().disable();

    http.csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .authorizeRequests(authorizeRequests -> authorizeRequests
            .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .antMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        )
        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(new JWTAuthenticationFilter(jwtSecret, jwtUtils), UsernamePasswordAuthenticationFilter.class);
}




    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
