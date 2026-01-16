package com.example.gym_management.dto;

import com.example.gym_management.entity.Payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment creation request")
public class PaymentRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private LocalDateTime paymentDate;

    @Schema(description = "Defaults to PENDING if not specified")
    private PaymentStatus status;
}
