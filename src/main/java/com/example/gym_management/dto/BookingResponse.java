package com.example.gym_management.dto;

import com.example.gym_management.entity.Booking.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Booking details")
public class BookingResponse {

    private Long id;
    private MemberDTO member;
    private ScheduledClassResponse scheduledClass;
    private BookingStatus status;

    @Schema(description = "When the booked class starts")
    private LocalDateTime classStartTime;

    private String className;
    private String trainerName;
    private String roomName;
}
