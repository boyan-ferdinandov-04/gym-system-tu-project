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

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 255)
  private String specialization;

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  public Trainer(String name, String specialization) {
    this.name = name;
    this.specialization = specialization;
  }
}
