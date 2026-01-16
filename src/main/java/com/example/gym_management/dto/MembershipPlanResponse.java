package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Membership plan details")
public class MembershipPlanResponse {

    private Long id;

    @Schema(description = "Plan tier name", example = "Premium")
    private String tierName;

    private BigDecimal price;

    @Schema(description = "Plan duration in days")
    private Integer durationDays;
}
