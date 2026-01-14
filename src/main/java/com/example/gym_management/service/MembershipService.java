package com.example.gym_management.service;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.mapper.MembershipPlanMapper;
import com.example.gym_management.repository.MembershipPlanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class MembershipService {

  private final MembershipPlanRepository membershipPlanRepository;
  private final MembershipPlanMapper membershipPlanMapper;

  @Transactional
  public MembershipPlanResponse createMembershipPlan(@Valid MembershipPlanRequest request) {
    MembershipPlan membershipPlan = membershipPlanMapper.toEntity(request);
    MembershipPlan saved = membershipPlanRepository.save(membershipPlan);
    return membershipPlanMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public MembershipPlanResponse getMembershipPlanById(Long id) {
    MembershipPlan plan = membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));
    return membershipPlanMapper.toResponse(plan);
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getAllMembershipPlans() {
    return membershipPlanMapper.toResponseList(membershipPlanRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getAvailableMembershipPlans() {
    return membershipPlanMapper.toResponseList(membershipPlanRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> searchMembershipPlansByTierName(String tierName) {
    if (tierName == null || tierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Tier name cannot be null or empty");
    }
    return membershipPlanMapper.toResponseList(membershipPlanRepository.findByTierNameContainingIgnoreCase(tierName));
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getMembershipPlansByMaxDuration(Integer maxDurationDays) {
    if (maxDurationDays == null || maxDurationDays <= 0) {
      throw new IllegalArgumentException("Max duration days must be a positive integer");
    }
    return membershipPlanMapper.toResponseList(membershipPlanRepository.findByDurationDaysLessThanEqual(maxDurationDays));
  }

  @Transactional
  public MembershipPlanResponse updateMembershipPlan(Long id, @Valid MembershipPlanRequest request) {
    MembershipPlan existingPlan = membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));

    membershipPlanMapper.updateEntity(request, existingPlan);

    MembershipPlan updated = membershipPlanRepository.save(existingPlan);
    return membershipPlanMapper.toResponse(updated);
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
