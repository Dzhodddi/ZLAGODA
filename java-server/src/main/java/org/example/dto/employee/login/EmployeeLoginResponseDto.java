package org.example.dto.employee.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeLoginResponseDto(@JsonProperty("access_token") String accessToken,
                                       @JsonProperty("refresh_token") String refreshToken) {
}
