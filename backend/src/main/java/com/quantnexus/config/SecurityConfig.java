package com.quantnexus.config;

import com.quantnexus.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Master Security Configuration.
 * Handled: CSRF Disable, Stateless Session, JWT Filter, and Route Protection.
 * @author Manish Singh
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())
                //Setting the permission -> firewall rules
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to Auth and Documentation
                        .requestMatchers(
                                "/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        // Allow H2 Console for local development
                        .requestMatchers("/h2-console/**").permitAll()
                        // Secure everything else
                        .anyRequest().authenticated()
                )
                //Setting Session to Stateless (No JSESSIONID cookies)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Wiring up the Auth Provider and the JWT Interceptor
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                //Allow H2 Console to load in iframes (SameOrigin)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}