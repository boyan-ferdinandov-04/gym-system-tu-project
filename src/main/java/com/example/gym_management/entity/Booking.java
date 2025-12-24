package com.example.gym_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_id", nullable = false)
  private ScheduledClass scheduledClass;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status;

  public Booking() {
  }

  public Booking(Member member, ScheduledClass scheduledClass, BookingStatus status) {
    this.member = member;
    this.scheduledClass = scheduledClass;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Member getMember() {
    return member;
  }

  public void setMember(Member member) {
    this.member = member;
  }

  public ScheduledClass getScheduledClass() {
    return scheduledClass;
  }

  public void setScheduledClass(ScheduledClass scheduledClass) {
    this.scheduledClass = scheduledClass;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    this.status = status;
  }

  public enum BookingStatus {
    ENROLLED,
    CANCELLED
  }
}
