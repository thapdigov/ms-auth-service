package az.kapital.msauthservice.service;

import az.kapital.msauthservice.exception.InvalidCredentialsException;
import az.kapital.msauthservice.exception.InvalidTokenException;
import az.kapital.msauthservice.model.request.UserLoginRequest;
import az.kapital.msauthservice.model.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(authenticationManager, jwtService);
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenCredentialsAreValid() {

        UserLoginRequest request = new UserLoginRequest("Sanan@example.com", "Sanan123");
        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getName()).thenReturn("Sanan@example.com");
        when(jwtService.generateToken("Sanan@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Success", response.getMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken("Sanan@example.com");
    }

    @Test
    void authenticate_ShouldThrowInvalidCredentialsException_WhenAuthenticationFails() {
        UserLoginRequest request = new UserLoginRequest("Bahruz@example.com", "badpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(mock(AuthenticationException.class));

        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(request));
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void validateToken_ShouldReturnUsername_WhenTokenIsValid() {
        String token = "valid-token";
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("Bahruz@example.com");

        String username = authService.validateToken(token);

        assertEquals("Bahruz@example.com", username);
        verify(jwtService, times(1)).validateToken(token);
        verify(jwtService, times(1)).extractUsername(token);
    }

    @Test
    void validateToken_ShouldThrowInvalidTokenException_WhenTokenIsInvalid() {
        String token = "invalid-token";
        when(jwtService.validateToken(token)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
        verify(jwtService, times(1)).validateToken(token);
        verify(jwtService, never()).extractUsername(anyString());
    }
}
