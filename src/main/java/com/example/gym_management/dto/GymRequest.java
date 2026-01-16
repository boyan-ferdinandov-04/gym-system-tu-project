package com.example.gym_management.dto;

import com.example.gym_management.entity.GymStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymRequest {

    @NotBlank(message = "Gym name is required")
    @Size(max = 100, message = "Gym name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private GymStatus status;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
