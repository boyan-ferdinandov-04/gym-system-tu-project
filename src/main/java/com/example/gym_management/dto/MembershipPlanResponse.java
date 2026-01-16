package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanResponse {

  private Long id;
  private String tierName;
  private BigDecimal price;
  private Integer durationDays;
  private List<GymDTO> accessibleGyms;
}
