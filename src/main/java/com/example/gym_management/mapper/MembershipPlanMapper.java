package com.example.gym_management.mapper;

import com.example.gym_management.dto.GymDTO;
import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MembershipPlanMapper {

    private final GymRepository gymRepository;

    public MembershipPlanResponse toResponse(MembershipPlan membershipPlan) {
        if (membershipPlan == null) {
            return null;
        }

        List<GymDTO> gymDTOs = membershipPlan.getAccessibleGyms() != null
                ? membershipPlan.getAccessibleGyms().stream()
                    .map(gym -> new GymDTO(gym.getId(), gym.getName()))
                    .collect(Collectors.toList())
                : null;

        return new MembershipPlanResponse(
                membershipPlan.getId(),
                membershipPlan.getTierName(),
                membershipPlan.getPrice(),
                membershipPlan.getDurationDays(),
                gymDTOs
        );
    }

    public List<MembershipPlanResponse> toResponseList(List<MembershipPlan> membershipPlans) {
        return membershipPlans.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MembershipPlan toEntity(MembershipPlanRequest request) {
        MembershipPlan membershipPlan = new MembershipPlan();
        membershipPlan.setTierName(request.getTierName());
        membershipPlan.setPrice(request.getPrice());
        membershipPlan.setDurationDays(request.getDurationDays());

        if (request.getAccessibleGymIds() != null && !request.getAccessibleGymIds().isEmpty()) {
            Set<Gym> gyms = resolveGyms(request.getAccessibleGymIds());
            membershipPlan.setAccessibleGyms(gyms);
        }

        return membershipPlan;
    }

    public void updateEntity(MembershipPlanRequest request, MembershipPlan membershipPlan) {
        membershipPlan.setTierName(request.getTierName());
        membershipPlan.setPrice(request.getPrice());
        membershipPlan.setDurationDays(request.getDurationDays());

        if (request.getAccessibleGymIds() != null) {
            if (request.getAccessibleGymIds().isEmpty()) {
                membershipPlan.getAccessibleGyms().clear();
            } else {
                Set<Gym> gyms = resolveGyms(request.getAccessibleGymIds());
                membershipPlan.setAccessibleGyms(gyms);
            }
        }
    }

    private Set<Gym> resolveGyms(Set<Long> gymIds) {
        Set<Gym> gyms = new HashSet<>(gymRepository.findAllById(gymIds));
        if (gyms.size() != gymIds.size()) {
            throw new IllegalArgumentException("One or more gym IDs are invalid");
        }
        return gyms;
    }
}
