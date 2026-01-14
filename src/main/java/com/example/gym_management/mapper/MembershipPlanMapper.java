package com.example.gym_management.mapper;

import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.entity.MembershipPlan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MembershipPlanMapper {

    public MembershipPlanResponse toResponse(MembershipPlan membershipPlan) {
        if (membershipPlan == null) {
            return null;
        }
        return new MembershipPlanResponse(
                membershipPlan.getId(),
                membershipPlan.getTierName(),
                membershipPlan.getPrice(),
                membershipPlan.getDurationDays()
        );
    }

    public List<MembershipPlanResponse> toResponseList(List<MembershipPlan> membershipPlans) {
        return membershipPlans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MembershipPlan toEntity(MembershipPlanRequest request) {
        return new MembershipPlan(
                request.getTierName(),
                request.getPrice(),
                request.getDurationDays()
        );
    }

    public void updateEntity(MembershipPlanRequest request, MembershipPlan membershipPlan) {
        membershipPlan.setTierName(request.getTierName());
        membershipPlan.setPrice(request.getPrice());
        membershipPlan.setDurationDays(request.getDurationDays());
    }
}
