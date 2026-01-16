package com.example.gym_management.dto;

import com.example.gym_management.entity.Payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment details")
public class PaymentResponse {

    private Long id;
    private MemberDTO member;
    private BigDecimal amount;
    private LocalDateTime paymentDate;

    @Schema(description = "PENDING, COMPLETED, FAILED, or REFUNDED")
    private PaymentStatus status;
}
