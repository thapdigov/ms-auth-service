package az.kapital.msauthservice.service;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        secretKey = Encoders.BASE64.encode("this-is-a-secure-test-key-1234567890".getBytes());

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    void generateToken_ShouldReturnValidJwt_WhenUsernameIsProvided() {
        String username = "testuser@example.com";

        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsTampered() {
        String validToken = jwtService.generateToken("user1");
        String tamperedToken = validToken + "x";

        boolean isValid = jwtService.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);
        String expiredToken = jwtService.generateToken("expiredUser");

        Thread.sleep(5);

        boolean isValid = jwtService.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void extractUsername_ShouldThrowException_WhenTokenInvalid() {
        String invalidToken = "not-a-real-jwt-token";

        assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
    }
}