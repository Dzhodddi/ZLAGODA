package org.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.employee.login.RefreshTokenRequestDto;
import org.example.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService service;

    private EmployeeLoginRequestDto loginRequest;
    private RefreshTokenRequestDto refreshTokenRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new EmployeeLoginRequestDto();
        loginRequest.setId_employee("EMP001");
        loginRequest.setPassword("password123");

        refreshTokenRequest = new RefreshTokenRequestDto();
        refreshTokenRequest.setRefreshToken("validRefreshToken");
    }

    @Test
    @DisplayName("authenticate should return tokens for valid credentials")
    void authenticate_validCredentials_shouldReturnTokens() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP001");
        when(jwtUtil.generateAccessToken("EMP001")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken("EMP001")).thenReturn("refreshToken");

        EmployeeLoginResponseDto result = service.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateAccessToken("EMP001");
        verify(jwtUtil, times(1)).generateRefreshToken("EMP001");
    }

    @Test
    @DisplayName("authenticate should throw exception for invalid credentials")
    void authenticate_invalidCredentials_shouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> service.authenticate(loginRequest));
        verify(jwtUtil, never()).generateAccessToken(anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("authenticate should use correct employee ID and password")
    void authenticate_shouldUseCorrectCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP001");
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");

        service.authenticate(loginRequest);

        verify(authenticationManager).authenticate(argThat(token ->
                token.getPrincipal().equals("EMP001") &&
                        token.getCredentials().equals("password123")
        ));
    }

    @Test
    @DisplayName("refreshToken should return new tokens for valid refresh token")
    void refreshToken_validToken_shouldReturnNewTokens() {
        when(jwtUtil.isValidToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.getUsername("validRefreshToken")).thenReturn("EMP001");
        when(jwtUtil.generateAccessToken("EMP001")).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken("EMP001")).thenReturn("newRefreshToken");

        EmployeeLoginResponseDto result = service.refreshToken(refreshTokenRequest);

        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
        verify(jwtUtil, times(1)).isValidToken("validRefreshToken");
        verify(jwtUtil, times(1)).getUsername("validRefreshToken");
        verify(jwtUtil, times(1)).generateAccessToken("EMP001");
        verify(jwtUtil, times(1)).generateRefreshToken("EMP001");
    }

    @Test
    @DisplayName("refreshToken should throw AuthenticationException for invalid token")
    void refreshToken_invalidToken_shouldThrowException() {
        when(jwtUtil.isValidToken("invalidToken")).thenReturn(false);
        refreshTokenRequest.setRefreshToken("invalidToken");

        assertThrows(AuthenticationException.class, () -> service.refreshToken(refreshTokenRequest));
        verify(jwtUtil, never()).getUsername(anyString());
        verify(jwtUtil, never()).generateAccessToken(anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    @DisplayName("refreshToken should throw AuthenticationException for expired token")
    void refreshToken_expiredToken_shouldThrowException() {
        when(jwtUtil.isValidToken("expiredToken")).thenReturn(false);
        refreshTokenRequest.setRefreshToken("expiredToken");

        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> service.refreshToken(refreshTokenRequest));

        assertTrue(exception.getMessage().contains("Invalid or expired refresh token"));
    }

    @Test
    @DisplayName("authenticate should generate different access and refresh tokens")
    void authenticate_shouldGenerateDifferentTokens() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("EMP001");
        when(jwtUtil.generateAccessToken("EMP001")).thenReturn("accessToken123");
        when(jwtUtil.generateRefreshToken("EMP001")).thenReturn("refreshToken456");

        EmployeeLoginResponseDto result = service.authenticate(loginRequest);

        assertNotEquals(result.accessToken(), result.refreshToken());
    }

    @Test
    @DisplayName("refreshToken should extract username from token")
    void refreshToken_shouldExtractUsernameFromToken() {
        when(jwtUtil.isValidToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.getUsername("validRefreshToken")).thenReturn("EMP002");
        when(jwtUtil.generateAccessToken("EMP002")).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken("EMP002")).thenReturn("newRefreshToken");

        service.refreshToken(refreshTokenRequest);

        verify(jwtUtil, times(1)).getUsername("validRefreshToken");
        verify(jwtUtil, times(1)).generateAccessToken("EMP002");
        verify(jwtUtil, times(1)).generateRefreshToken("EMP002");
    }
}
