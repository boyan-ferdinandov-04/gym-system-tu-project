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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gym_id", nullable = false)
  private Gym gym;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  @ManyToMany
  @JoinTable(name = "trainer_class_types", joinColumns = @JoinColumn(name = "trainer_id"), inverseJoinColumns = @JoinColumn(name = "class_type_id"))
  private Set<ClassType> classTypes = new HashSet<>();

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TrainerAvailability> availabilities = new ArrayList<>();

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TrainerTimeOff> timeOffs = new ArrayList<>();

  public Trainer(Gym gym, String firstName, String lastName) {
    this.gym = gym;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Trainer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
