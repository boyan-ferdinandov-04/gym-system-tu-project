package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "membership_plan_id")
  private MembershipPlan membershipPlan;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Payment> payments;

  public Member(String firstName, String lastName, String email, MembershipPlan membershipPlan) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.membershipPlan = membershipPlan;
  }
}
