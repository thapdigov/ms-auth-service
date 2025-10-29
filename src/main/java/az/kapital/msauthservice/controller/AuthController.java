package az.kapital.msauthservice.controller;

import az.kapital.msauthservice.model.request.TokenValidationRequest;
import az.kapital.msauthservice.model.request.UserLoginRequest;
import az.kapital.msauthservice.model.request.UserRegistrationRequest;
import az.kapital.msauthservice.model.response.AuthResponse;
import az.kapital.msauthservice.service.AuthService;
import az.kapital.msauthservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationRequest request) {
        userService.saveUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid UserLoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody @Valid TokenValidationRequest request) {
        String username = authService.validateToken(request.getToken());
        return ResponseEntity.ok(username);
    }
}
