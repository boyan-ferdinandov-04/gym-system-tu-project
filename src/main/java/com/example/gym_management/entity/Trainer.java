package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainers")
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

  public Trainer() {
  }

  public Trainer(String name, String specialization) {
    this.name = name;
    this.specialization = specialization;
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

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public List<ScheduledClass> getScheduledClasses() {
    return scheduledClasses;
  }

  public void setScheduledClasses(List<ScheduledClass> scheduledClasses) {
    this.scheduledClasses = scheduledClasses;
  }
}
