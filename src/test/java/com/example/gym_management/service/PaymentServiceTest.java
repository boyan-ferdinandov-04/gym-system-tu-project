package com.example.gym_management.service;

import com.example.gym_management.dto.MemberDTO;
import com.example.gym_management.dto.PaymentRequest;
import com.example.gym_management.dto.PaymentResponse;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.Payment;
import com.example.gym_management.entity.Payment.PaymentStatus;
import com.example.gym_management.mapper.PaymentMapper;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private Member member;
    private MemberDTO memberDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        member = new Member("John", "Doe", "john@example.com", null);
        member.setId(1L);

        memberDTO = new MemberDTO(1L, "John", "Doe", "john@example.com");

        payment = new Payment(member, new BigDecimal("99.99"), now, PaymentStatus.PENDING);
        payment.setId(1L);

        paymentRequest = new PaymentRequest(1L, new BigDecimal("99.99"), now, PaymentStatus.PENDING);

        paymentResponse = new PaymentResponse(1L, memberDTO, new BigDecimal("99.99"), now, PaymentStatus.PENDING);
    }

    @Test
    void createPayment_Success() {
        when(paymentMapper.toEntity(paymentRequest)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.createPayment(paymentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(paymentRepository).save(payment);
    }

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.getPaymentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void getAllPayments_Success() {
        List<Payment> payments = List.of(payment);
        List<PaymentResponse> responses = List.of(paymentResponse);

        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.toResponseList(payments)).thenReturn(responses);

        List<PaymentResponse> result = paymentService.getAllPayments();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaymentsByMember_Success() {
        List<Payment> payments = List.of(payment);
        List<PaymentResponse> responses = List.of(paymentResponse);

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.findByMemberId(1L)).thenReturn(payments);
        when(paymentMapper.toResponseList(payments)).thenReturn(responses);

        List<PaymentResponse> result = paymentService.getPaymentsByMember(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaymentsByMember_MemberNotFound_ThrowsException() {
        when(memberRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.getPaymentsByMember(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    void getPaymentsByStatus_Success() {
        List<Payment> payments = List.of(payment);
        List<PaymentResponse> responses = List.of(paymentResponse);

        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(payments);
        when(paymentMapper.toResponseList(payments)).thenReturn(responses);

        List<PaymentResponse> result = paymentService.getPaymentsByStatus(PaymentStatus.PENDING);

        assertThat(result).hasSize(1);
    }

    @Test
    void deletePayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(1L);

        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_CompletedPayment_ThrowsException() {
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.deletePayment(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete completed payment");

        verify(paymentRepository, never()).delete(any());
    }

    @Test
    void refundPayment_Success() {
        payment.setStatus(PaymentStatus.COMPLETED);
        PaymentResponse refundedResponse = new PaymentResponse(1L, memberDTO, new BigDecimal("99.99"), now,
                PaymentStatus.REFUNDED);

        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(refundedResponse);

        PaymentResponse result = paymentService.refundPayment(1L);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void refundPayment_NotCompleted_ThrowsException() {
        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only completed payments can be refunded");
    }

    @Test
    void markPaymentAsCompleted_Success() {
        PaymentResponse completedResponse = new PaymentResponse(1L, memberDTO, new BigDecimal("99.99"), now,
                PaymentStatus.COMPLETED);

        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(completedResponse);

        PaymentResponse result = paymentService.markPaymentAsCompleted(1L);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void markPaymentAsCompleted_AlreadyCompleted_ThrowsException() {
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.markPaymentAsCompleted(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already completed");
    }

    @Test
    void markPaymentAsFailed_Success() {
        PaymentResponse failedResponse = new PaymentResponse(1L, memberDTO, new BigDecimal("99.99"), now,
                PaymentStatus.FAILED);

        when(paymentRepository.findByIdWithMember(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(failedResponse);

        PaymentResponse result = paymentService.markPaymentAsFailed(1L);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void getTotalRevenue_Success() {
        when(paymentRepository.sumAmountByStatus(PaymentStatus.COMPLETED)).thenReturn(new BigDecimal("999.99"));

        BigDecimal result = paymentService.getTotalRevenue();

        assertThat(result).isEqualByComparingTo(new BigDecimal("999.99"));
    }

    @Test
    void getMemberTotalPaid_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(paymentRepository.sumAmountByMemberIdAndStatus(1L, PaymentStatus.COMPLETED))
                .thenReturn(new BigDecimal("199.98"));

        BigDecimal result = paymentService.getMemberTotalPaid(1L);

        assertThat(result).isEqualByComparingTo(new BigDecimal("199.98"));
    }

    @Test
    void getMemberTotalPaid_MemberNotFound_ThrowsException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getMemberTotalPaid(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    void getPaymentsByDateRange_Success() {
        LocalDateTime startDate = now.minusDays(7);
        LocalDateTime endDate = now;
        List<Payment> payments = List.of(payment);
        List<PaymentResponse> responses = List.of(paymentResponse);

        when(paymentRepository.findByPaymentDateBetween(startDate, endDate)).thenReturn(payments);
        when(paymentMapper.toResponseList(payments)).thenReturn(responses);

        List<PaymentResponse> result = paymentService.getPaymentsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaymentsByDateRange_InvalidRange_ThrowsException() {
        LocalDateTime startDate = now;
        LocalDateTime endDate = now.minusDays(7);

        assertThatThrownBy(() -> paymentService.getPaymentsByDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before end date");
    }

    @Test
    void deletePayment_NotFound_ThrowsException() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.deletePayment(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment not found");
    }
}
