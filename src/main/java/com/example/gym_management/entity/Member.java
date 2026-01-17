package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members",
    indexes = {
        @Index(name = "idx_member_status", columnList = "membership_status"),
        @Index(name = "idx_member_end_date", columnList = "membership_end_date")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "membership_plan_id")
  private MembershipPlan membershipPlan;

  @Column(name = "membership_start_date")
  private LocalDate membershipStartDate;

  @Column(name = "membership_end_date")
  private LocalDate membershipEndDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "membership_status", length = 20)
  private MembershipStatus membershipStatus;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
  private List<Payment> payments;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @CreatedBy
  @Column(name = "created_by", length = 100)
  private String createdBy;

  @LastModifiedBy
  @Column(name = "modified_by", length = 100)
  private String modifiedBy;

  public Member(String firstName, String lastName, String email, MembershipPlan membershipPlan) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.membershipPlan = membershipPlan;
  }

  public boolean isExpired() {
    return membershipEndDate != null && LocalDate.now().isAfter(membershipEndDate);
  }

  public boolean isInGracePeriod(int gracePeriodDays) {
    if (membershipEndDate == null) return false;
    LocalDate gracePeriodEnd = membershipEndDate.plusDays(gracePeriodDays);
    LocalDate now = LocalDate.now();
    return now.isAfter(membershipEndDate) && !now.isAfter(gracePeriodEnd);
  }

  public boolean canBookNewClasses() {
    return membershipStatus == MembershipStatus.ACTIVE;
  }

  public enum MembershipStatus {
    PENDING, ACTIVE, GRACE_PERIOD, EXPIRED, SUSPENDED, CANCELLED
  }
}
