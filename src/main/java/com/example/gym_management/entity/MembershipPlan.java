package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "membership_plans")
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

  public MembershipPlan() {
  }

  public MembershipPlan(String tierName, BigDecimal price, Integer durationDays) {
    this.tierName = tierName;
    this.price = price;
    this.durationDays = durationDays;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTierName() {
    return tierName;
  }

  public void setTierName(String tierName) {
    this.tierName = tierName;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getDurationDays() {
    return durationDays;
  }

  public void setDurationDays(Integer durationDays) {
    this.durationDays = durationDays;
  }

  public List<Member> getMembers() {
    return members;
  }

  public void setMembers(List<Member> members) {
    this.members = members;
  }
}
