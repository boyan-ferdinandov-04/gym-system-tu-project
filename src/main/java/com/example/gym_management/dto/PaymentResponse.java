package com.example.gym_management.dto;

import com.example.gym_management.entity.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private MemberDTO member;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
}
