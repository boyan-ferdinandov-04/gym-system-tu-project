package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @ManyToMany(mappedBy = "classTypes")
  private Set<Trainer> trainers = new HashSet<>();

  public ClassType(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
