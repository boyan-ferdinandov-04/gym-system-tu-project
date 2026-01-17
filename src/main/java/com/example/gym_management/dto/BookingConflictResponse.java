package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Booking conflict response with waitlist info")
public class BookingConflictResponse {

    private String message;
    private String reason;
    private Long waitlistSize;
    private String waitlistEndpoint;
}
