package com.example.gym_management.service;

import com.example.gym_management.dto.PaymentRequest;
import com.example.gym_management.dto.PaymentResponse;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.Payment;
import com.example.gym_management.entity.Payment.PaymentStatus;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PaymentResponse createPayment(@Valid PaymentRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + request.getMemberId()));

        LocalDateTime paymentDate = request.getPaymentDate() != null ?
                request.getPaymentDate() : LocalDateTime.now();

        PaymentStatus status = request.getStatus() != null ?
                request.getStatus() : PaymentStatus.PENDING;

        Payment payment = new Payment(
                member,
                request.getAmount(),
                paymentDate,
                status
        );

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
        return PaymentResponse.fromEntity(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        return paymentRepository.findByMemberId(memberId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMemberAndStatus(Long memberId, PaymentStatus status) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }
        return paymentRepository.findByMemberIdAndStatus(memberId, status)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return paymentRepository.findByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMemberIdAndDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("Member not found with id: " + memberId);
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return paymentRepository.findByMemberIdAndDateRange(memberId, startDate, endDate)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByStatus(PaymentStatus status) {
        BigDecimal total = paymentRepository.sumAmountByStatus(status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getMemberTotalByStatus(Long memberId, PaymentStatus status) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        BigDecimal total = paymentRepository.sumAmountByMemberIdAndStatus(memberId, status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete completed payment. Use refund instead.");
        }

        paymentRepository.delete(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment refunded = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(refunded);
    }

    @Transactional
    public PaymentResponse markPaymentAsCompleted(Long id) {
        Payment payment = paymentRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment is already completed");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot complete a refunded payment");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        Payment completed = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(completed);
    }

    @Transactional
    public PaymentResponse markPaymentAsFailed(Long id) {
        Payment payment = paymentRepository.findByIdWithMember(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot mark completed payment as failed");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot mark refunded payment as failed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        Payment failed = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(failed);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal total = paymentRepository.sumAmountByStatus(PaymentStatus.COMPLETED);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getMemberTotalPaid(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        BigDecimal total = paymentRepository.sumAmountByMemberIdAndStatus(memberId, PaymentStatus.COMPLETED);
        return total != null ? total : BigDecimal.ZERO;
    }
}
