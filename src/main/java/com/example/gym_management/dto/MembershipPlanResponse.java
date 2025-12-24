package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanResponse {

  private Long id;
  private String tierName;
  private BigDecimal price;
  private Integer durationDays;

  public static MembershipPlanResponse fromEntity(com.example.gym_management.entity.MembershipPlan entity) {
    return new MembershipPlanResponse(
        entity.getId(),
        entity.getTierName(),
        entity.getPrice(),
        entity.getDurationDays()
    );
  }
}
