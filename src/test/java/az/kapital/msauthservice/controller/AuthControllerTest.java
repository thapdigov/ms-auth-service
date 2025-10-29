package az.kapital.msauthservice.controller;

import az.kapital.msauthservice.model.request.TokenValidationRequest;
import az.kapital.msauthservice.model.request.UserLoginRequest;
import az.kapital.msauthservice.model.response.AuthResponse;
import az.kapital.msauthservice.service.AuthService;
import az.kapital.msauthservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Test
    void authenticateUser_Success_ReturnsToken() throws Exception {
        UserLoginRequest request = new UserLoginRequest("user@example.com", "password123");
        AuthResponse mockResponse = new AuthResponse("mock.jwt.token", "Success");

        when(authService.authenticate(any(UserLoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    @Test
    void validateToken_ValidToken_ReturnsUsername() throws Exception {
        TokenValidationRequest request = new TokenValidationRequest("valid.jwt.token");
        String expectedUsername = "test_user";

        when(authService.validateToken(any(String.class))).thenReturn(expectedUsername);

        mockMvc.perform(post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUsername));
    }
}