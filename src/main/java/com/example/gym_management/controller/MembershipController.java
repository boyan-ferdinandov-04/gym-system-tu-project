package com.example.gym_management.controller;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

  private final MembershipService membershipService;

  @PostMapping
  public ResponseEntity<MembershipPlanResponse> createMembershipPlan(
      @Valid @RequestBody MembershipPlanRequest request) {
    MembershipPlanResponse response = membershipService.createMembershipPlan(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MembershipPlanResponse> getMembershipPlanById(@PathVariable Long id) {
    MembershipPlanResponse response = membershipService.getMembershipPlanById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<MembershipPlanResponse>> getAllMembershipPlans() {
    List<MembershipPlanResponse> plans = membershipService.getAllMembershipPlans();
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/available")
  public ResponseEntity<List<MembershipPlanResponse>> getAvailableMembershipPlans() {
    List<MembershipPlanResponse> plans = membershipService.getAvailableMembershipPlans();
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/search")
  public ResponseEntity<List<MembershipPlanResponse>> searchByTierName(
      @RequestParam String tierName) {
    List<MembershipPlanResponse> plans = membershipService.searchMembershipPlansByTierName(tierName);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/filter")
  public ResponseEntity<List<MembershipPlanResponse>> filterByMaxDuration(
      @RequestParam Integer maxDurationDays) {
    List<MembershipPlanResponse> plans = membershipService.getMembershipPlansByMaxDuration(maxDurationDays);
    return ResponseEntity.ok(plans);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MembershipPlanResponse> updateMembershipPlan(
      @PathVariable Long id,
      @Valid @RequestBody MembershipPlanRequest request) {
    MembershipPlanResponse response = membershipService.updateMembershipPlan(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMembershipPlan(@PathVariable Long id) {
    membershipService.deleteMembershipPlan(id);
    return ResponseEntity.noContent().build();
  }
}
