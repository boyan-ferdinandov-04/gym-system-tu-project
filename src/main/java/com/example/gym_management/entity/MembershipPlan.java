package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tier_name", nullable = false, length = 100)
  private String tierName;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "duration_days", nullable = false)
  private Integer durationDays;

  @OneToMany(mappedBy = "membershipPlan", cascade = CascadeType.ALL)
  private List<Member> members;

  public MembershipPlan(String tierName, BigDecimal price, Integer durationDays) {
    this.tierName = tierName;
    this.price = price;
    this.durationDays = durationDays;
  }
}
