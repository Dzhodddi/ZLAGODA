package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.dto.employee.login.EmployeeLoginRequestDto;
import org.example.dto.employee.login.EmployeeLoginResponseDto;
import org.example.dto.employee.login.RefreshTokenRequestDto;
import org.example.exception.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public EmployeeLoginResponseDto authenticate(EmployeeLoginRequestDto request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getId_employee(),
                        request.getPassword()
                )
        );
        String username = authentication.getName();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        return new EmployeeLoginResponseDto(accessToken, refreshToken);
    }

    public EmployeeLoginResponseDto refreshToken(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.isValidToken(refreshToken)) {
            throw new AuthenticationException("Invalid or expired refresh token: " + refreshToken);
        }
        String username = jwtUtil.getUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        return new EmployeeLoginResponseDto(newAccessToken, newRefreshToken);
    }
}