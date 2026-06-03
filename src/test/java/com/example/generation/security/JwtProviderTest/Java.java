package com.example.generation.security.JwtProviderTest;

import com.example.generation.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret",
                "testSecretKeyThatIsLongEnoughForHmacSha256Algorithm!!");
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtProvider.generateToken("test@test.com");
        assertNotNull(token);
    }

    @Test
    void generateToken_tokenStartsWithEyJ() {
        String token = jwtProvider.generateToken("test@test.com");
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void getUsernameFromToken_returnsCorrectEmail() {
        String email = "test@test.com";
        String token = jwtProvider.generateToken(email);
        assertEquals(email, jwtProvider.getUsernameFromToken(token));
    }

    @Test
    void validateToken_validTokenReturnsTrue() {
        String token = jwtProvider.generateToken("test@test.com");
        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_manipulatedTokenReturnsFalse() {
        String token = jwtProvider.generateToken("test@test.com");
        assertFalse(jwtProvider.validateToken(token + "tampered"));
    }
}