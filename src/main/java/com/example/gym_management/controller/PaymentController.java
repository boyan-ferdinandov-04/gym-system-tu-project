package com.example.gym_management.controller;

import com.example.gym_management.dto.PaymentRequest;
import com.example.gym_management.dto.PaymentResponse;
import com.example.gym_management.entity.Payment.PaymentStatus;
import com.example.gym_management.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
    PaymentResponse response = paymentService.createPayment(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
    PaymentResponse response = paymentService.getPaymentById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAllPayments() {
    List<PaymentResponse> payments = paymentService.getAllPayments();
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/member/{memberId}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByMember(@PathVariable Long memberId) {
    List<PaymentResponse> payments = paymentService.getPaymentsByMember(memberId);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
    List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/member/{memberId}/status/{status}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByMemberAndStatus(
      @PathVariable Long memberId,
      @PathVariable PaymentStatus status) {
    List<PaymentResponse> payments = paymentService.getPaymentsByMemberAndStatus(memberId, status);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/date-range")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<PaymentResponse> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/member/{memberId}/date-range")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByMemberIdAndDateRange(
      @PathVariable Long memberId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<PaymentResponse> payments = paymentService.getPaymentsByMemberIdAndDateRange(memberId, startDate, endDate);
    return ResponseEntity.ok(payments);
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<PaymentResponse> markPaymentAsCompleted(@PathVariable Long id) {
    PaymentResponse response = paymentService.markPaymentAsCompleted(id);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/fail")
  public ResponseEntity<PaymentResponse> markPaymentAsFailed(@PathVariable Long id) {
    PaymentResponse response = paymentService.markPaymentAsFailed(id);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/refund")
  public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
    PaymentResponse response = paymentService.refundPayment(id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
    paymentService.deletePayment(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/revenue/total")
  public ResponseEntity<BigDecimal> getTotalRevenue() {
    BigDecimal total = paymentService.getTotalRevenue();
    return ResponseEntity.ok(total);
  }

  @GetMapping("/revenue/status/{status}")
  public ResponseEntity<BigDecimal> getTotalRevenueByStatus(@PathVariable PaymentStatus status) {
    BigDecimal total = paymentService.getTotalRevenueByStatus(status);
    return ResponseEntity.ok(total);
  }

  @GetMapping("/member/{memberId}/total")
  public ResponseEntity<BigDecimal> getMemberTotalPaid(@PathVariable Long memberId) {
    BigDecimal total = paymentService.getMemberTotalPaid(memberId);
    return ResponseEntity.ok(total);
  }

  @GetMapping("/member/{memberId}/total/status/{status}")
  public ResponseEntity<BigDecimal> getMemberTotalByStatus(
      @PathVariable Long memberId,
      @PathVariable PaymentStatus status) {
    BigDecimal total = paymentService.getMemberTotalByStatus(memberId, status);
    return ResponseEntity.ok(total);
  }
}
