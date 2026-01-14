package com.example.gym_management.mapper;

import com.example.gym_management.dto.MemberDTO;
import com.example.gym_management.dto.MemberRequest;
import com.example.gym_management.dto.MemberResponse;
import com.example.gym_management.entity.Booking;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;

    public MemberDTO toSimpleDto(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberDTO(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail()
        );
    }

    public MemberResponse toResponse(Member member) {
        if (member == null) {
            return null;
        }

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                membershipPlanMapper.toResponse(member.getMembershipPlan()),
                calculateActiveBookingsCount(member)
        );
    }

    public MemberResponse toResponseWithoutBookingCount(Member member) {
        if (member == null) {
            return null;
        }

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                membershipPlanMapper.toResponse(member.getMembershipPlan()),
                null
        );
    }

    public List<MemberResponse> toResponseListWithoutBookingCount(List<Member> members) {
        return members.stream()
                .map(this::toResponseWithoutBookingCount)
                .collect(Collectors.toList());
    }

    public Member toEntity(MemberRequest request) {
        MembershipPlan membershipPlan = null;
        if (request.getMembershipPlanId() != null) {
            membershipPlan = membershipPlanRepository.findById(request.getMembershipPlanId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Membership plan not found with id: " + request.getMembershipPlanId()));
        }

        return new Member(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                membershipPlan
        );
    }

    public void updateEntity(MemberRequest request, Member member) {
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());

        if (request.getMembershipPlanId() != null) {
            MembershipPlan membershipPlan = membershipPlanRepository.findById(request.getMembershipPlanId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Membership plan not found with id: " + request.getMembershipPlanId()));
            member.setMembershipPlan(membershipPlan);
        } else {
            member.setMembershipPlan(null);
        }
    }

    private Integer calculateActiveBookingsCount(Member member) {
        if (member.getBookings() == null) {
            return 0;
        }
        return (int) member.getBookings().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.ENROLLED)
                .count();
    }
}
