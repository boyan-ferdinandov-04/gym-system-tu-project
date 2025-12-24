package com.example.gym_management.service;

import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

  private final MembershipPlanRepository membershipPlanRepository;

  @Transactional
  public MembershipPlan createMembershipPlan(String tierName, BigDecimal price, Integer durationDays) {
    validateMembershipPlan(tierName, price, durationDays);

    MembershipPlan membershipPlan = new MembershipPlan(tierName, price, durationDays);
    return membershipPlanRepository.save(membershipPlan);
  }

  @Transactional(readOnly = true)
  public MembershipPlan getMembershipPlanById(Long id) {
    return membershipPlanRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public List<MembershipPlan> getAllMembershipPlans() {
    return membershipPlanRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<MembershipPlan> getAvailableMembershipPlans() {
    return membershipPlanRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<MembershipPlan> searchMembershipPlansByTierName(String tierName) {
    if (tierName == null || tierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Tier name cannot be null or empty");
    }
    return membershipPlanRepository.findByTierNameContainingIgnoreCase(tierName);
  }

  @Transactional(readOnly = true)
  public List<MembershipPlan> getMembershipPlansByMaxDuration(Integer maxDurationDays) {
    if (maxDurationDays == null || maxDurationDays <= 0) {
      throw new IllegalArgumentException("Max duration days must be a positive integer");
    }
    return membershipPlanRepository.findByDurationDaysLessThanEqual(maxDurationDays);
  }

  @Transactional
  public MembershipPlan updateMembershipPlan(Long id, String tierName, BigDecimal price, Integer durationDays) {
    MembershipPlan existingPlan = getMembershipPlanById(id);

    validateMembershipPlan(tierName, price, durationDays);

    existingPlan.setTierName(tierName);
    existingPlan.setPrice(price);
    existingPlan.setDurationDays(durationDays);

    return membershipPlanRepository.save(existingPlan);
  }

  @Transactional
  public void deleteMembershipPlan(Long id) {
    MembershipPlan existingPlan = getMembershipPlanById(id);

    if (existingPlan.getMembers() != null && !existingPlan.getMembers().isEmpty()) {
      throw new IllegalStateException("Cannot delete membership plan with active members. " +
          "Please reassign or remove members first.");
    }

    membershipPlanRepository.delete(existingPlan);
  }

  // Placeholder method will replace with DTO
  private void validateMembershipPlan(String tierName, BigDecimal price, Integer durationDays) {
    if (tierName == null || tierName.trim().isEmpty()) {
      throw new IllegalArgumentException("Tier name cannot be null or empty");
    }

    if (tierName.length() > 100) {
      throw new IllegalArgumentException("Tier name cannot exceed 100 characters");
    }

    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be positive");
    }

    if (price.scale() > 2) {
      throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
    }

    if (durationDays == null) {
      throw new IllegalArgumentException("Duration days cannot be null");
    }

    if (durationDays <= 0) {
      throw new IllegalArgumentException("Duration days must be a positive integer");
    }

    if (durationDays > 3650) {
      throw new IllegalArgumentException("Duration days cannot exceed 3650 days (10 years)");
    }
  }
}
