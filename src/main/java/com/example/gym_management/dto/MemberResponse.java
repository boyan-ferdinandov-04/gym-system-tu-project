package com.example.gym_management.dto;

import com.example.gym_management.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private MembershipPlanResponse membershipPlan;
    private Integer activeBookingsCount;

    public static MemberResponse fromEntity(Member member) {
        if (member == null) {
            return null;
        }

        Integer activeBookings = 0;
        if (member.getBookings() != null) {
            activeBookings = (int) member.getBookings().stream()
                    .filter(b -> b.getStatus() == com.example.gym_management.entity.Booking.BookingStatus.ENROLLED)
                    .count();
        }

        MembershipPlanResponse planResponse = null;
        if (member.getMembershipPlan() != null) {
            planResponse = MembershipPlanResponse.fromEntity(member.getMembershipPlan());
        }

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                planResponse,
                activeBookings
        );
    }

    public static MemberResponse fromEntityWithoutBookingCount(Member member) {
        if (member == null) {
            return null;
        }

        MembershipPlanResponse planResponse = null;
        if (member.getMembershipPlan() != null) {
            planResponse = MembershipPlanResponse.fromEntity(member.getMembershipPlan());
        }

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                planResponse,
                null
        );
    }
}
