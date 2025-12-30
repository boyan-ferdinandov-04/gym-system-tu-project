package com.example.gym_management.controller;

import com.example.gym_management.dto.MemberRequest;
import com.example.gym_management.dto.MemberResponse;
import com.example.gym_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> response = memberService.getAllMembers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberResponse>> searchMembersByName(@RequestParam String name) {
        List<MemberResponse> response = memberService.searchMembersByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-membership-plan/{membershipPlanId}")
    public ResponseEntity<List<MemberResponse>> getMembersByMembershipPlan(
            @PathVariable Long membershipPlanId) {
        List<MemberResponse> response = memberService.getMembersByMembershipPlan(membershipPlanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/without-plan")
    public ResponseEntity<List<MemberResponse>> getMembersWithoutPlan() {
        List<MemberResponse> response = memberService.getMembersWithoutPlan();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}/assign-plan/{membershipPlanId}")
    public ResponseEntity<MemberResponse> assignMembershipPlan(
            @PathVariable Long memberId,
            @PathVariable Long membershipPlanId) {
        MemberResponse response = memberService.assignMembershipPlan(memberId, membershipPlanId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}/remove-plan")
    public ResponseEntity<MemberResponse> removeMembershipPlan(@PathVariable Long memberId) {
        MemberResponse response = memberService.removeMembershipPlan(memberId);
        return ResponseEntity.ok(response);
    }
}
