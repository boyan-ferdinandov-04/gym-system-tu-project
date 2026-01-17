package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Waitlist position information")
public class WaitlistPositionResponse {

    private Long waitlistId;
    private Long memberId;
    private Long scheduledClassId;

    @Schema(description = "Position in queue (1-based)")
    private Long position;

    @Schema(description = "Total number of people waiting")
    private Long totalWaiting;
}
