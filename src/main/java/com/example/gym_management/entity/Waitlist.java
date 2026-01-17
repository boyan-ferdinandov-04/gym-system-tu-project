package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlists",
    indexes = {
        @Index(name = "idx_waitlist_scheduled_class", columnList = "scheduled_class_id"),
        @Index(name = "idx_waitlist_member", columnList = "member_id"),
        @Index(name = "idx_waitlist_status", columnList = "status")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_class_status",
            columnNames = {"member_id", "scheduled_class_id", "status"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scheduled_class_id", nullable = false)
  private ScheduledClass scheduledClass;

  @Column(name = "joined_at", nullable = false)
  private LocalDateTime joinedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WaitlistStatus status;

  @Column(name = "notified_at")
  private LocalDateTime notifiedAt;

  @Version
  private Long version;

  public Waitlist(Member member, ScheduledClass scheduledClass) {
    this.member = member;
    this.scheduledClass = scheduledClass;
    this.joinedAt = LocalDateTime.now();
    this.status = WaitlistStatus.WAITING;
  }

  public enum WaitlistStatus {
    WAITING,
    PROMOTED,
    EXPIRED,
    REMOVED
  }
}
