package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  @ManyToMany
  @JoinTable(name = "trainer_class_types", joinColumns = @JoinColumn(name = "trainer_id"), inverseJoinColumns = @JoinColumn(name = "class_type_id"))
  private Set<ClassType> classTypes = new HashSet<>();

  public Trainer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
