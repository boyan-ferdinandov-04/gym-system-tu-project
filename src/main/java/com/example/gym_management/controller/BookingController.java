package com.example.gym_management.controller;

import com.example.gym_management.dto.BookingDTOs.BookingEligibility;
import com.example.gym_management.dto.BookingDTOs.ClassAvailability;
import com.example.gym_management.dto.BookingRequest;
import com.example.gym_management.dto.BookingResponse;
import com.example.gym_management.entity.Booking.BookingStatus;
import com.example.gym_management.service.BookingService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Class booking management operations")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Create a booking", description = "Creates a new class booking for a member. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or class full", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member or scheduled class not found", content = @Content)
    })
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieves a booking by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    public ResponseEntity<BookingResponse> getBookingById(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieves all bookings in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class))))
    })
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> response = bookingService.getAllBookings();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Cancel a booking", description = "Cancels an existing booking. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    public ResponseEntity<BookingResponse> cancelBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/re-enroll")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Re-enroll a cancelled booking", description = "Re-enrolls a previously cancelled booking. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking re-enrolled successfully",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cannot re-enroll - class full or past", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    public ResponseEntity<BookingResponse> reEnrollBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        BookingResponse response = bookingService.reEnrollBooking(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a booking", description = "Permanently deletes a booking. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get bookings by member", description = "Retrieves all bookings for a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<BookingResponse>> getBookingsByMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/active")
    @Operation(summary = "Get active bookings by member", description = "Retrieves all active (enrolled) bookings for a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<BookingResponse>> getActiveBookingsByMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getActiveBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/upcoming")
    @Operation(summary = "Get upcoming bookings by member", description = "Retrieves upcoming bookings for a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<BookingResponse>> getUpcomingBookingsByMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getUpcomingBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}/past")
    @Operation(summary = "Get past bookings by member", description = "Retrieves past bookings for a specific member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Past bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<BookingResponse>> getPastBookingsByMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<BookingResponse> response = bookingService.getPastBookingsByMember(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{scheduledClassId}")
    @Operation(summary = "Get bookings by scheduled class", description = "Retrieves all bookings for a specific scheduled class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
    })
    public ResponseEntity<List<BookingResponse>> getBookingsByScheduledClass(
            @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long scheduledClassId) {
        List<BookingResponse> response = bookingService.getBookingsByScheduledClass(scheduledClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get bookings by status", description = "Retrieves all bookings with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class))))
    })
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(
            @Parameter(description = "Booking status (ENROLLED, CANCELLED, COMPLETED)", required = true) @PathVariable BookingStatus status) {
        List<BookingResponse> response = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get bookings by date range", description = "Retrieves all bookings within a specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class))))
    })
    public ResponseEntity<List<BookingResponse>> getBookingsByDateRange(
            @Parameter(description = "Start date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BookingResponse> response = bookingService.getBookingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{scheduledClassId}/availability")
    @Operation(summary = "Get class availability", description = "Retrieves availability information for a scheduled class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ClassAvailability.class))),
            @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
    })
    public ResponseEntity<ClassAvailability> getClassAvailability(
            @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long scheduledClassId) {
        ClassAvailability availability = bookingService.getClassAvailability(scheduledClassId);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/check-eligibility")
    @Operation(summary = "Check booking eligibility", description = "Checks if a member is eligible to book a specific class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eligibility check completed",
                    content = @Content(schema = @Schema(implementation = BookingEligibility.class))),
            @ApiResponse(responseCode = "404", description = "Member or class not found", content = @Content)
    })
    public ResponseEntity<BookingEligibility> checkBookingEligibility(
            @Parameter(description = "Member ID", required = true) @RequestParam Long memberId,
            @Parameter(description = "Scheduled Class ID", required = true) @RequestParam Long scheduledClassId) {
        BookingEligibility eligibility = bookingService.checkBookingEligibility(memberId, scheduledClassId);
        return ResponseEntity.ok(eligibility);
    }

    @PostMapping("/class/{scheduledClassId}/cancel-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel all bookings for a class", description = "Cancels all bookings for a scheduled class. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookings cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> cancelAllBookingsForClass(
            @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long scheduledClassId) {
        int cancelledCount = bookingService.cancelAllBookingsForClass(scheduledClassId);
        return ResponseEntity.ok(Map.of(
                "scheduledClassId", scheduledClassId,
                "cancelledBookings", cancelledCount,
                "message", "Successfully cancelled " + cancelledCount + " booking(s)"));
    }
}
