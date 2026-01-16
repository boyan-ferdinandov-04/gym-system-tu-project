package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gym creation/update request")
public class GymRequest {

    @NotBlank(message = "Gym name is required")
    @Size(max = 100, message = "Gym name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
}
