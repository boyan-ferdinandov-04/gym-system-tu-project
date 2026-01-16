package com.example.gym_management.controller;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.service.MembershipService;
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

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Membership Plans", description = "Membership plan management operations")
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create membership plan", description = "Creates a new membership plan. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Membership plan created successfully",
                    content = @Content(schema = @Schema(implementation = MembershipPlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<MembershipPlanResponse> createMembershipPlan(
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipService.createMembershipPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership plan by ID", description = "Retrieves a membership plan by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership plan found",
                    content = @Content(schema = @Schema(implementation = MembershipPlanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership plan not found", content = @Content)
    })
    public ResponseEntity<MembershipPlanResponse> getMembershipPlanById(
            @Parameter(description = "Membership Plan ID", required = true) @PathVariable Long id) {
        MembershipPlanResponse response = membershipService.getMembershipPlanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all membership plans", description = "Retrieves all membership plans in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership plans retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MembershipPlanResponse.class))))
    })
    public ResponseEntity<List<MembershipPlanResponse>> getAllMembershipPlans() {
        List<MembershipPlanResponse> plans = membershipService.getAllMembershipPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available membership plans", description = "Retrieves all currently available membership plans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available plans retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MembershipPlanResponse.class))))
    })
    public ResponseEntity<List<MembershipPlanResponse>> getAvailableMembershipPlans() {
        List<MembershipPlanResponse> plans = membershipService.getAvailableMembershipPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/search")
    @Operation(summary = "Search membership plans", description = "Searches for membership plans by tier name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MembershipPlanResponse.class))))
    })
    public ResponseEntity<List<MembershipPlanResponse>> searchByTierName(
            @Parameter(description = "Tier name to search for", required = true) @RequestParam String tierName) {
        List<MembershipPlanResponse> plans = membershipService.searchMembershipPlansByTierName(tierName);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter by max duration", description = "Retrieves membership plans with duration up to the specified maximum days.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered plans retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MembershipPlanResponse.class))))
    })
    public ResponseEntity<List<MembershipPlanResponse>> filterByMaxDuration(
            @Parameter(description = "Maximum duration in days", required = true) @RequestParam Integer maxDurationDays) {
        List<MembershipPlanResponse> plans = membershipService.getMembershipPlansByMaxDuration(maxDurationDays);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update membership plan", description = "Updates an existing membership plan. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership plan updated successfully",
                    content = @Content(schema = @Schema(implementation = MembershipPlanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Membership plan not found", content = @Content)
    })
    public ResponseEntity<MembershipPlanResponse> updateMembershipPlan(
            @Parameter(description = "Membership Plan ID", required = true) @PathVariable Long id,
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipService.updateMembershipPlan(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete membership plan", description = "Deletes a membership plan. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Membership plan deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Membership plan not found", content = @Content)
    })
    public ResponseEntity<Void> deleteMembershipPlan(
            @Parameter(description = "Membership Plan ID", required = true) @PathVariable Long id) {
        membershipService.deleteMembershipPlan(id);
        return ResponseEntity.noContent().build();
    }
}
