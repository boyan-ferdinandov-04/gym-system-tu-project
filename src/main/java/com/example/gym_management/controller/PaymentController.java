package com.example.gym_management.controller;

import com.example.gym_management.dto.PaymentRequest;
import com.example.gym_management.dto.PaymentResponse;
import com.example.gym_management.entity.Payment.PaymentStatus;
import com.example.gym_management.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and revenue tracking operations")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Create a payment", description = "Records a new payment for a member. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID", required = true) @PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves all payments in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class))))
    })
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get payments by member", description = "Retrieves all payments for a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieves all payments with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class))))
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(
            @Parameter(description = "Payment status (PENDING, COMPLETED, FAILED, REFUNDED)", required = true) @PathVariable PaymentStatus status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/member/{memberId}/status/{status}")
    @Operation(summary = "Get payments by member and status", description = "Retrieves payments for a member with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMemberAndStatus(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId,
            @Parameter(description = "Payment status", required = true) @PathVariable PaymentStatus status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByMemberAndStatus(memberId, status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range", description = "Retrieves all payments within a specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class))))
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
            @Parameter(description = "Start date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PaymentResponse> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/member/{memberId}/date-range")
    @Operation(summary = "Get member payments by date range", description = "Retrieves payments for a member within a specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<PaymentResponse>> getPaymentsByMemberIdAndDateRange(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId,
            @Parameter(description = "Start date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PaymentResponse> payments = paymentService.getPaymentsByMemberIdAndDateRange(memberId, startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark payment as completed", description = "Marks a pending payment as completed. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment marked as completed",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<PaymentResponse> markPaymentAsCompleted(
            @Parameter(description = "Payment ID", required = true) @PathVariable Long id) {
        PaymentResponse response = paymentService.markPaymentAsCompleted(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fail")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark payment as failed", description = "Marks a payment as failed. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment marked as failed",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<PaymentResponse> markPaymentAsFailed(
            @Parameter(description = "Payment ID", required = true) @PathVariable Long id) {
        PaymentResponse response = paymentService.markPaymentAsFailed(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Refund payment", description = "Refunds a completed payment. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment refunded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment state for refund", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<PaymentResponse> refundPayment(
            @Parameter(description = "Payment ID", required = true) @PathVariable Long id) {
        PaymentResponse response = paymentService.refundPayment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete payment", description = "Deletes a payment record. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "Payment ID", required = true) @PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/revenue/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get total revenue", description = "Retrieves total revenue from all completed payments. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total revenue retrieved",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal total = paymentService.getTotalRevenue();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/revenue/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get revenue by status", description = "Retrieves total revenue for payments with a specific status. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revenue retrieved",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<BigDecimal> getTotalRevenueByStatus(
            @Parameter(description = "Payment status", required = true) @PathVariable PaymentStatus status) {
        BigDecimal total = paymentService.getTotalRevenueByStatus(status);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/member/{memberId}/total")
    @Operation(summary = "Get member total paid", description = "Retrieves total amount paid by a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total retrieved",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<BigDecimal> getMemberTotalPaid(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        BigDecimal total = paymentService.getMemberTotalPaid(memberId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/member/{memberId}/total/status/{status}")
    @Operation(summary = "Get member total by status", description = "Retrieves total amount for a member's payments with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total retrieved",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<BigDecimal> getMemberTotalByStatus(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId,
            @Parameter(description = "Payment status", required = true) @PathVariable PaymentStatus status) {
        BigDecimal total = paymentService.getMemberTotalByStatus(memberId, status);
        return ResponseEntity.ok(total);
    }
}
