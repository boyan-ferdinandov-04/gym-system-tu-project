package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Column(length = 255)
  private String specialization;

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  public Trainer(String firstName, String lastName, String specialization) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
  }
}
