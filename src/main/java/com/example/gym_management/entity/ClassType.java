package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "class_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

  public ClassType(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
