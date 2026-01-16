package com.example.gym_management.dto;

import com.example.gym_management.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private UserRole role;
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
