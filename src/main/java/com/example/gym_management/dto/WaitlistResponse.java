package com.example.gym_management.dto;

import com.example.gym_management.entity.Waitlist.WaitlistStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Waitlist entry details")
public class WaitlistResponse {

    private Long id;
    private MemberDTO member;
    private ScheduledClassResponse scheduledClass;
    private WaitlistStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime notifiedAt;

    @Schema(description = "When the class starts")
    private LocalDateTime classStartTime;

    private String className;
}
