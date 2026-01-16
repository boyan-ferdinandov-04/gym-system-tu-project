package com.example.gym_management.dto;

import com.example.gym_management.entity.GymStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gym details")
public class GymResponse {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private GymStatus status;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
