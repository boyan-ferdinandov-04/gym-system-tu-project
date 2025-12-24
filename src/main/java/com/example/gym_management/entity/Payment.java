package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Column(name = "payment_date", nullable = false)
  private LocalDateTime paymentDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  public Payment() {
  }

  public Payment(Member member, BigDecimal amount, LocalDateTime paymentDate, PaymentStatus status) {
    this.member = member;
    this.amount = amount;
    this.paymentDate = paymentDate;
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

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public enum PaymentStatus {
    COMPLETED,
    PENDING,
    FAILED,
    REFUNDED
  }
}
