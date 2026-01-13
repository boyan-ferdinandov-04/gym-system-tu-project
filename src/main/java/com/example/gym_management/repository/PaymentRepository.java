package com.example.gym_management.repository;

import com.example.gym_management.entity.Payment;
import com.example.gym_management.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberId(Long memberId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.status = :status")
    List<Payment> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    List<Payment> findByMemberIdAndDateRange(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.member WHERE p.id = :id")
    Optional<Payment> findByIdWithMember(@Param("id") Long id);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.member.id = :memberId AND p.status = :status")
    BigDecimal sumAmountByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.paymentDate < :thresholdDate")
    List<Payment> findOverduePendingPayments(@Param("thresholdDate") LocalDateTime thresholdDate);
}
