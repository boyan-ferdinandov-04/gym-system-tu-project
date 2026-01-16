package com.example.gym_management.controller;

import com.example.gym_management.dto.MemberRequest;
import com.example.gym_management.dto.MemberResponse;
import com.example.gym_management.service.MemberService;
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
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Gym member management operations")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Create a new member", description = "Registers a new gym member. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created successfully",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
    })
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Retrieves a member by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<MemberResponse> getMemberById(
            @Parameter(description = "Member ID", required = true) @PathVariable Long id) {
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all members", description = "Retrieves all gym members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class))))
    })
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> response = memberService.getAllMembers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Update member", description = "Updates an existing member's information. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated successfully",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete member", description = "Removes a member from the system. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "Member ID", required = true) @PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search members by name", description = "Searches for members by their first or last name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class))))
    })
    public ResponseEntity<List<MemberResponse>> searchMembersByName(
            @Parameter(description = "Name to search for", required = true) @RequestParam String name) {
        List<MemberResponse> response = memberService.searchMembersByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-membership-plan/{membershipPlanId}")
    @Operation(summary = "Get members by membership plan", description = "Retrieves all members subscribed to a specific membership plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Membership plan not found", content = @Content)
    })
    public ResponseEntity<List<MemberResponse>> getMembersByMembershipPlan(
            @Parameter(description = "Membership Plan ID", required = true) @PathVariable Long membershipPlanId) {
        List<MemberResponse> response = memberService.getMembersByMembershipPlan(membershipPlanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/without-plan")
    @Operation(summary = "Get members without plan", description = "Retrieves all members who don't have an active membership plan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class))))
    })
    public ResponseEntity<List<MemberResponse>> getMembersWithoutPlan() {
        List<MemberResponse> response = memberService.getMembersWithoutPlan();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}/assign-plan/{membershipPlanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Assign membership plan", description = "Assigns a membership plan to a member. Requires ADMIN, MANAGER, or EMPLOYEE role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership plan assigned successfully",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member or plan not found", content = @Content)
    })
    public ResponseEntity<MemberResponse> assignMembershipPlan(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId,
            @Parameter(description = "Membership Plan ID", required = true) @PathVariable Long membershipPlanId) {
        MemberResponse response = memberService.assignMembershipPlan(memberId, membershipPlanId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}/remove-plan")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Remove membership plan", description = "Removes the membership plan from a member. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership plan removed successfully",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)
    })
    public ResponseEntity<MemberResponse> removeMembershipPlan(
            @Parameter(description = "Member ID", required = true) @PathVariable Long memberId) {
        MemberResponse response = memberService.removeMembershipPlan(memberId);
        return ResponseEntity.ok(response);
    }
}
