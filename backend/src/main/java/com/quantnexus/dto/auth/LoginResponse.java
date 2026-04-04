package com.quantnexus.dto.auth;

import com.quantnexus.domain.enums.BaseCurrency;

/**
 * Rich payload for successful authentication events.
 * Beyond delivering the JWT, this DTO provisions the frontend with immediate
 * user context (Role, Currency, Identity) to hydrate the initial UI state
 * without requiring secondary API round-trips.
 * * @author Manish Singh
 */
public record LoginResponse(
        String token,          // The cryptographically signed JWT Bearer token
        String email,          // Unique user identifier
        String fullName,       // For personalized "Welcome, Manish" UI
        String role,           // Enables immediate client-side RBAC (Role-Based Access Control) routing
        BaseCurrency currency, // Provides "INR" for data and "₹" for UI symbols
        String message         // Human-readable status or personalized greeting
) {}