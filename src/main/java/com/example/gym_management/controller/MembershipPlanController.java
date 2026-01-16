package com.example.gym_management.controller;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlanResponse> createMembershipPlan(
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipPlanService.createMembershipPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipPlanResponse> getMembershipPlanById(@PathVariable Long id) {
        MembershipPlanResponse response = membershipPlanService.getMembershipPlanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MembershipPlanResponse>> getAllMembershipPlans() {
        List<MembershipPlanResponse> responses = membershipPlanService.getAllMembershipPlans();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MembershipPlanResponse>> searchByTierName(@RequestParam String tierName) {
        List<MembershipPlanResponse> responses = membershipPlanService.searchByTierName(tierName);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlanResponse> updateMembershipPlan(
            @PathVariable Long id,
            @Valid @RequestBody MembershipPlanRequest request) {
        MembershipPlanResponse response = membershipPlanService.updateMembershipPlan(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMembershipPlan(@PathVariable Long id) {
        membershipPlanService.deleteMembershipPlan(id);
        return ResponseEntity.noContent().build();
    }
}
