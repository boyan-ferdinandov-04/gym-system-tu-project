package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "membership_plan_id")
  private MembershipPlan membershipPlan;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Payment> payments;

  public Member() {
  }

  public Member(String name, String email, MembershipPlan membershipPlan) {
    this.name = name;
    this.email = email;
    this.membershipPlan = membershipPlan;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public MembershipPlan getMembershipPlan() {
    return membershipPlan;
  }

  public void setMembershipPlan(MembershipPlan membershipPlan) {
    this.membershipPlan = membershipPlan;
  }

  public List<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }

  public List<Payment> getPayments() {
    return payments;
  }

  public void setPayments(List<Payment> payments) {
    this.payments = payments;
  }
}
