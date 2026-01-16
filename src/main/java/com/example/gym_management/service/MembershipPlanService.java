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
public class MembershipPlanService {

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
        MembershipPlan membershipPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));
        return membershipPlanMapper.toResponse(membershipPlan);
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanResponse> getAllMembershipPlans() {
        return membershipPlanMapper.toResponseList(membershipPlanRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanResponse> searchByTierName(String tierName) {
        return membershipPlanMapper.toResponseList(
                membershipPlanRepository.findByTierNameContainingIgnoreCase(tierName));
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
        MembershipPlan membershipPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + id));

        if (membershipPlan.getMembers() != null && !membershipPlan.getMembers().isEmpty()) {
            throw new IllegalStateException("Cannot delete membership plan with active members. " +
                    "Please reassign members to a different plan first.");
        }

        membershipPlanRepository.delete(membershipPlan);
    }
}
