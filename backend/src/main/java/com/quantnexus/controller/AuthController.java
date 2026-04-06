package com.quantnexus.controller;

import com.quantnexus.dto.auth.LoginRequest;
import com.quantnexus.dto.auth.RegisterRequest;
import com.quantnexus.service.AuthService;
import com.quantnexus.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Entry point for User Authentication and Registration.
 * Accessible without JWT for these specific endpoints.
 * @author Manish Singh
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimitingService   rateLimitingService;


    //Registers a new user and returns their profile summary.
    @PostMapping("/register")
    public ResponseEntity<?>registerUser(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
            ){
        String ipAddress = httpRequest.getRemoteAddr();

        log.info("API Request: Registering new user");

        // 🛡️ Redis Anti-Spam Check
        if(!rateLimitingService.allowLoginAttempt(ipAddress)){
            return ResponseEntity.status(429).body("Spam Protection: Registration limit reached for this IP. Try again in 1 hour.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    //Authenticates a user and returns a JWT Bearer token.
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("API Request: User login");

        // 🛡️ Redis Anti-Brute-Force Check
        if(!rateLimitingService.allowLoginAttempt(request.email())){
            return ResponseEntity.status(429)
                    .body("Security Block: Maximum login attempts reached. Please wait 60 seconds.");
        }

        return ResponseEntity.ok(authService.login(request));
    }

    //Manually Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>>logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. See you soon! 👋"));
    }

}
