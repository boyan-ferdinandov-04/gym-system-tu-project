package com.example.gym_management.dto;

import java.time.LocalDateTime;

public final class BookingDTOs {

  private BookingDTOs() {
  }

  public record ClassAvailability(
      Long scheduledClassId,
      String className,
      LocalDateTime startTime,
      Integer totalCapacity,
      Integer currentEnrollments,
      Integer availableSpots,
      boolean isAvailable) {
  }

  public record BookingEligibility(
      Long memberId,
      Long scheduledClassId,
      boolean eligible,
      String reason) {
  }
}
