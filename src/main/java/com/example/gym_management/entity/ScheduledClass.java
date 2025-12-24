package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scheduled_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledClass {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_type_id", nullable = false)
  private ClassType classType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trainer_id", nullable = false)
  private Trainer trainer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @OneToMany(mappedBy = "scheduledClass", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  public ScheduledClass(ClassType classType, Trainer trainer, Room room, LocalDateTime startTime) {
    this.classType = classType;
    this.trainer = trainer;
    this.room = room;
    this.startTime = startTime;
  }
}
