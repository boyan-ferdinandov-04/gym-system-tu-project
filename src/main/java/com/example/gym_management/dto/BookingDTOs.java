package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public final class BookingDTOs {

    private BookingDTOs() {
    }

    @Schema(description = "Class availability information")
    public record ClassAvailability(
            Long scheduledClassId,
            String className,
            LocalDateTime startTime,
            Integer totalCapacity,
            @Schema(description = "Current number of enrollments")
            Integer currentEnrollments,
            @Schema(description = "Remaining spots available")
            Integer availableSpots,
            @Schema(description = "Whether the class can accept bookings")
            boolean isAvailable) {
    }

    @Schema(description = "Booking eligibility check result")
    public record BookingEligibility(
            Long memberId,
            Long scheduledClassId,
            @Schema(description = "Whether the member can book this class")
            boolean eligible,
            @Schema(description = "Reason if not eligible")
            String reason) {
    }
}
