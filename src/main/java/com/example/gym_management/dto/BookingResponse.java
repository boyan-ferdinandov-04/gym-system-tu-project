package com.example.gym_management.dto;

import com.example.gym_management.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private MemberDTO member;
    private ScheduledClassResponse scheduledClass;
    private BookingStatus status;
    private LocalDateTime classStartTime;
    private String className;
    private String trainerName;
    private String roomName;
}
