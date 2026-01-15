package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "trainer_time_off")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTimeOff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trainer_id", nullable = false)
  private Trainer trainer;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "reason", length = 255)
  private String reason;

  public TrainerTimeOff(Trainer trainer, LocalDate date, String reason) {
    this.trainer = trainer;
    this.date = date;
    this.reason = reason;
  }
}
