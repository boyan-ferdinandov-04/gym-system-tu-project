package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Schema(description = "Number of currently active bookings")
    private Integer activeBookingsCount;
}
