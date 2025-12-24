package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "class_types")
public class ClassType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @OneToMany(mappedBy = "classType", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  public ClassType() {
  }

  public ClassType(String name, String description) {
    this.name = name;
    this.description = description;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ScheduledClass> getScheduledClasses() {
    return scheduledClasses;
  }

  public void setScheduledClasses(List<ScheduledClass> scheduledClasses) {
    this.scheduledClasses = scheduledClasses;
  }
}
