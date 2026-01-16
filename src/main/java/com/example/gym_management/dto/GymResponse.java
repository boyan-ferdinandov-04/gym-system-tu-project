package com.example.gym_management.dto;

import com.example.gym_management.entity.GymStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
