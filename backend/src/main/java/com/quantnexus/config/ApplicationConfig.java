package com.quantnexus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bootstraps the core security mechanics.
 * Wires our custom JPA user service to the native AuthenticationManager.
 * @author Manish Singh
 */

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // Spring automatically finds our JpaUserDetailsService because it has @Service
    // We do not need to inject concrete class instead we inject interface
    // @Service automatically tells about implementation part
    private final UserDetailsService userDetailsService;

    /**
     * The Data Access Object (DAO) provider.
     * It uses our JPA service to find the user and the BCrypt encoder to verify the password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(userDetailsService);
        daoAuthProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthProvider;
    }

    /**
     * The global Authentication Manager.
     * We inject this into our AuthService to trigger the login verification.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    /**
     * Password hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
