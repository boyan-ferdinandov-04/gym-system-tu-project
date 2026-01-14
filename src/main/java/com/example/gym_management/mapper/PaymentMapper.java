package com.example.gym_management.mapper;

import com.example.gym_management.dto.PaymentRequest;
import com.example.gym_management.dto.PaymentResponse;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.Payment;
import com.example.gym_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
                payment.getId(),
                memberMapper.toSimpleDto(payment.getMember()),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }

    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        return payments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Payment toEntity(PaymentRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + request.getMemberId()));

        LocalDateTime paymentDate = request.getPaymentDate() != null
                ? request.getPaymentDate()
                : LocalDateTime.now();

        Payment.PaymentStatus status = request.getStatus() != null
                ? request.getStatus()
                : Payment.PaymentStatus.PENDING;

        return new Payment(
                member,
                request.getAmount(),
                paymentDate,
                status
        );
    }

    public void updateEntity(PaymentRequest request, Payment payment) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found with id: " + request.getMemberId()));

        payment.setMember(member);
        payment.setAmount(request.getAmount());

        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }

        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());
        }
    }
}
