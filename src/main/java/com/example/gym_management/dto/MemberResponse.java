package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Member details")
public class MemberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private MembershipPlanResponse membershipPlan;

    @Schema(description = "Membership start date")
    private LocalDate membershipStartDate;

    @Schema(description = "Membership end date")
    private LocalDate membershipEndDate;

    @Schema(description = "Current membership status")
    private String membershipStatus;

    @Schema(description = "Days until membership expiration (null if no end date, 0 if already expired)")
    private Integer daysUntilExpiration;

    @Schema(description = "Whether the membership has expired")
    private Boolean isExpired;

    @Schema(description = "Number of currently active bookings")
    private Integer activeBookingsCount;
}
