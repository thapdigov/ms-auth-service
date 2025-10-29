package az.kapital.msauthservice.service;

import az.kapital.msauthservice.exception.InvalidCredentialsException;
import az.kapital.msauthservice.exception.InvalidTokenException;
import az.kapital.msauthservice.model.request.UserLoginRequest;
import az.kapital.msauthservice.model.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse authenticate(UserLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtService.generateToken(authentication.getName());
            return new AuthResponse(token, "Login successful");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    public String validateToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new InvalidTokenException("Token is invalid or expired");
        }
        return jwtService.extractUsername(token);
    }
}

