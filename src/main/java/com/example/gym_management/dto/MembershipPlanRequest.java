package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Membership plan creation/update request")
public class MembershipPlanRequest {

    @NotBlank(message = "Tier name is required")
    @Size(max = 100, message = "Tier name cannot exceed 100 characters")
    @Schema(description = "Plan tier name", example = "Premium")
    private String tierName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

  @NotNull(message = "Duration is required")
  @Min(value = 1, message = "Duration must be at least 1 day")
  @Max(value = 3650, message = "Duration cannot exceed 3650 days (10 years)")
  private Integer durationDays;

  @NotEmpty(message = "At least one accessible gym is required")
  private Set<Long> accessibleGymIds;
}
