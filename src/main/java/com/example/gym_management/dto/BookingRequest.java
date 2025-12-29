package com.example.gym_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Scheduled class ID is required")
    private Long scheduledClassId;
}
