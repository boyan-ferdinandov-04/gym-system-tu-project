package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scheduled_classes")
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

  public ScheduledClass() {
  }

  public ScheduledClass(ClassType classType, Trainer trainer, Room room, LocalDateTime startTime) {
    this.classType = classType;
    this.trainer = trainer;
    this.room = room;
    this.startTime = startTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ClassType getClassType() {
    return classType;
  }

  public void setClassType(ClassType classType) {
    this.classType = classType;
  }

  public Trainer getTrainer() {
    return trainer;
  }

  public void setTrainer(Trainer trainer) {
    this.trainer = trainer;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public List<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }
}
