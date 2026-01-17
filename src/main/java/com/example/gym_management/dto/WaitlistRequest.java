package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Waitlist join request")
public class WaitlistRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Scheduled class ID is required")
    private Long scheduledClassId;
}
