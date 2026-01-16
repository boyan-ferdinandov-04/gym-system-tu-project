package com.example.gym_management.dto;

import com.example.gym_management.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response with JWT tokens")
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration in seconds", example = "3600")
    private Long expiresIn;

    private String username;
    private UserRole role;

    @Schema(description = "Associated gym ID (null for ADMIN)")
    private Long gymId;

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn,
                        String username, UserRole role, Long gymId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
        this.gymId = gymId;
    }
}
