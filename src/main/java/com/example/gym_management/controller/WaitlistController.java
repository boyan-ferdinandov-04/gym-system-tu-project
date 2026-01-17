package com.example.gym_management.controller;

import com.example.gym_management.dto.WaitlistPositionResponse;
import com.example.gym_management.dto.WaitlistRequest;
import com.example.gym_management.dto.WaitlistResponse;
import com.example.gym_management.service.WaitlistService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waitlists")
@RequiredArgsConstructor
@Tag(name = "Waitlists", description = "Class waitlist management operations")
public class WaitlistController {

    private final WaitlistService waitlistService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Add member to waitlist",
               description = "Adds a member to the waitlist for a full class. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Added to waitlist successfully",
            content = @Content(schema = @Schema(implementation = WaitlistResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or eligibility issue", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
        @ApiResponse(responseCode = "404", description = "Member or class not found", content = @Content)
    })
    public ResponseEntity<WaitlistResponse> addToWaitlist(@Valid @RequestBody WaitlistRequest request) {
        WaitlistResponse response = waitlistService.addToWaitlist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Remove from waitlist",
               description = "Removes a member from the waitlist. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Removed from waitlist successfully",
            content = @Content(schema = @Schema(implementation = WaitlistResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
        @ApiResponse(responseCode = "404", description = "Waitlist entry not found", content = @Content)
    })
    public ResponseEntity<WaitlistResponse> removeFromWaitlist(
            @Parameter(description = "Waitlist entry ID", required = true) @PathVariable Long id) {
        WaitlistResponse response = waitlistService.removeFromWaitlist(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/position")
    @Operation(summary = "Get waitlist position",
               description = "Gets member's position in the waitlist queue.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Position retrieved successfully",
            content = @Content(schema = @Schema(implementation = WaitlistPositionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Waitlist entry not found", content = @Content)
    })
    public ResponseEntity<WaitlistPositionResponse> getWaitlistPosition(
            @Parameter(description = "Waitlist entry ID", required = true) @PathVariable Long id) {
        WaitlistPositionResponse response = waitlistService.getWaitlistPosition(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{scheduledClassId}")
    @Operation(summary = "Get class waitlist",
               description = "Retrieves all active waitlist entries for a class.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waitlist retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WaitlistResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Class not found", content = @Content)
    })
    public ResponseEntity<List<WaitlistResponse>> getWaitlistByClass(
            @Parameter(description = "Scheduled class ID", required = true) @PathVariable Long scheduledClassId) {
        List<WaitlistResponse> response = waitlistService.getWaitlistByClass(scheduledClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get member's waitlists",
               description = "Retrieves all active waitlist entries for a member.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Member waitlists retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WaitlistResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<List<WaitlistResponse>> getMemberWaitlists(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        List<WaitlistResponse> response = waitlistService.getMemberWaitlists(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/expire")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Expire old waitlist entries",
               description = "Batch cleanup of waitlist entries for started classes. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waitlist entries expired successfully", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> expireWaitlistEntries() {
        int expiredCount = waitlistService.expireWaitlistEntries();
        return ResponseEntity.ok(Map.of(
            "expiredCount", expiredCount,
            "message", "Successfully expired " + expiredCount + " waitlist entry(ies)"));
    }
}
