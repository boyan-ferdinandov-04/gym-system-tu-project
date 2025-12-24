package com.example.gym_management.service;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.repository.MembershipPlanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class MembershipService {

  private final MembershipPlanRepository membershipPlanRepository;

  @Transactional
  public MembershipPlanResponse createMembershipPlan(@Valid MembershipPlanRequest request) {
    MembershipPlan membershipPlan = new MembershipPlan(
        request.getTierName(),
        request.getPrice(),
        request.getDurationDays()
    );
    MembershipPlan saved = membershipPlanRepository.save(membershipPlan);
    return MembershipPlanResponse.fromEntity(saved);
  }

  @Transactional(readOnly = true)
  public MembershipPlanResponse getMembershipPlanById(Long id) {
    MembershipPlan plan = membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));
    return MembershipPlanResponse.fromEntity(plan);
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getAllMembershipPlans() {
    return membershipPlanRepository.findAll()
        .stream()
        .map(MembershipPlanResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getAvailableMembershipPlans() {
    return membershipPlanRepository.findAll()
        .stream()
        .map(MembershipPlanResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> searchMembershipPlansByTierName(String tierName) {
    if (tierName == null || tierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Tier name cannot be null or empty");
    }
    return membershipPlanRepository.findByTierNameContainingIgnoreCase(tierName)
        .stream()
        .map(MembershipPlanResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getMembershipPlansByMaxDuration(Integer maxDurationDays) {
    if (maxDurationDays == null || maxDurationDays <= 0) {
      throw new IllegalArgumentException("Max duration days must be a positive integer");
    }
    return membershipPlanRepository.findByDurationDaysLessThanEqual(maxDurationDays)
        .stream()
        .map(MembershipPlanResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional
  public MembershipPlanResponse updateMembershipPlan(Long id, @Valid MembershipPlanRequest request) {
    MembershipPlan existingPlan = membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));

    existingPlan.setTierName(request.getTierName());
    existingPlan.setPrice(request.getPrice());
    existingPlan.setDurationDays(request.getDurationDays());

    MembershipPlan updated = membershipPlanRepository.save(existingPlan);
    return MembershipPlanResponse.fromEntity(updated);
  }

  @Transactional
  public void deleteMembershipPlan(Long id) {
    MembershipPlan existingPlan = membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));

    if (existingPlan.getMembers() != null && !existingPlan.getMembers().isEmpty()) {
      throw new IllegalStateException("Cannot delete membership plan with active members. " +
          "Please reassign or remove members first.");
    }

    membershipPlanRepository.delete(existingPlan);
  }
}
