package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gyms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gym {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 255)
  private String address;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(length = 100)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GymStatus status = GymStatus.ACTIVE;

  @Column(name = "opening_time")
  private LocalTime openingTime;

  @Column(name = "closing_time")
  private LocalTime closingTime;

  @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
  private List<Room> rooms = new ArrayList<>();

  @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
  private List<Trainer> trainers = new ArrayList<>();

  @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses = new ArrayList<>();

  @ManyToMany(mappedBy = "accessibleGyms")
  private Set<MembershipPlan> membershipPlans = new HashSet<>();

  public Gym(String name, String address, String phoneNumber) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.status = GymStatus.ACTIVE;
  }

  public Gym(String name, String address, String phoneNumber, String email,
             GymStatus status, LocalTime openingTime, LocalTime closingTime) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.status = status;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
  }
}
