package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Membership plan details")
public class MembershipPlanResponse {

  private Long id;
  private String tierName;
  private BigDecimal price;
  private Integer durationDays;
  private List<GymDTO> accessibleGyms;
}
