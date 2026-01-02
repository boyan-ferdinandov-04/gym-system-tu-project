package com.example.gym_management.controller;

import com.example.gym_management.dto.BookingDTOs.BookingEligibility;
import com.example.gym_management.dto.BookingDTOs.ClassAvailability;
import com.example.gym_management.dto.BookingRequest;
import com.example.gym_management.dto.BookingResponse;
import com.example.gym_management.entity.Booking.BookingStatus;
import com.example.gym_management.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> response = bookingService.getAllBookings();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/re-enroll")
    public ResponseEntity<BookingResponse> reEnrollBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.reEnrollBooking(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByMember(@PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookingsByMember(@PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getActiveBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/upcoming")
    public ResponseEntity<List<BookingResponse>> getUpcomingBookingsByMember(@PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getUpcomingBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/past")
    public ResponseEntity<List<BookingResponse>> getPastBookingsByMember(@PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getPastBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{scheduledClassId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByScheduledClass(
            @PathVariable Long scheduledClassId) {
        List<BookingResponse> response = bookingService.getBookingsByScheduledClass(scheduledClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<BookingResponse> response = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BookingResponse>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BookingResponse> response = bookingService.getBookingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{scheduledClassId}/availability")
    public ResponseEntity<ClassAvailability> getClassAvailability(@PathVariable Long scheduledClassId) {
        ClassAvailability availability = bookingService.getClassAvailability(scheduledClassId);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/check-eligibility")
    public ResponseEntity<BookingEligibility> checkBookingEligibility(
            @RequestParam Long memberId,
            @RequestParam Long scheduledClassId) {
        BookingEligibility eligibility = bookingService.checkBookingEligibility(memberId, scheduledClassId);
        return ResponseEntity.ok(eligibility);
    }

    @PostMapping("/class/{scheduledClassId}/cancel-all")
    public ResponseEntity<Map<String, Object>> cancelAllBookingsForClass(
            @PathVariable Long scheduledClassId) {
        int cancelledCount = bookingService.cancelAllBookingsForClass(scheduledClassId);
        return ResponseEntity.ok(Map.of(
                "scheduledClassId", scheduledClassId,
                "cancelledBookings", cancelledCount,
                "message", "Successfully cancelled " + cancelledCount + " booking(s)"
        ));
    }
}
