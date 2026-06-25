package com.securebanking.controller;

import com.securebanking.dto.LoginRequest;
import com.securebanking.dto.LoginResponse;
import com.securebanking.entity.User;
import com.securebanking.security.JwtUtil;
import com.securebanking.service.AuditService;
import com.securebanking.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuditService auditService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetailsService.findByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getId());

        auditService.log("LOGIN", user.getUsername(), null, httpRequest);

        LoginResponse response = new LoginResponse(
                token, user.getUsername(), user.getRole().name(), user.getId(), user.getFullName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                auditService.log("LOGOUT", username, null, httpRequest);
            } catch (Exception ignored) {}
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
