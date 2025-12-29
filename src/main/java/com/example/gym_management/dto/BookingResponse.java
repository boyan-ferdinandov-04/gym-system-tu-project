package com.example.gym_management.dto;

import com.example.gym_management.entity.Booking;
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

    public static BookingResponse fromEntity(Booking booking) {
        if (booking == null) {
            return null;
        }

        String className = null;
        String trainerName = null;
        String roomName = null;
        LocalDateTime classStartTime = null;

        if (booking.getScheduledClass() != null) {
            classStartTime = booking.getScheduledClass().getStartTime();

            if (booking.getScheduledClass().getClassType() != null) {
                className = booking.getScheduledClass().getClassType().getName();
            }

            if (booking.getScheduledClass().getTrainer() != null) {
                trainerName = booking.getScheduledClass().getTrainer().getFirstName() + " " +
                        booking.getScheduledClass().getTrainer().getLastName();
            }

            if (booking.getScheduledClass().getRoom() != null) {
                roomName = booking.getScheduledClass().getRoom().getRoomName();
            }
        }

        return new BookingResponse(
                booking.getId(),
                MemberDTO.fromEntity(booking.getMember()),
                ScheduledClassResponse.fromEntity(booking.getScheduledClass()),
                booking.getStatus(),
                classStartTime,
                className,
                trainerName,
                roomName
        );
    }

    public static BookingResponse fromEntityCompact(Booking booking) {
        if (booking == null) {
            return null;
        }

        String className = null;
        String trainerName = null;
        String roomName = null;
        LocalDateTime classStartTime = null;

        if (booking.getScheduledClass() != null) {
            classStartTime = booking.getScheduledClass().getStartTime();

            if (booking.getScheduledClass().getClassType() != null) {
                className = booking.getScheduledClass().getClassType().getName();
            }

            if (booking.getScheduledClass().getTrainer() != null) {
                trainerName = booking.getScheduledClass().getTrainer().getFirstName() + " " +
                        booking.getScheduledClass().getTrainer().getLastName();
            }

            if (booking.getScheduledClass().getRoom() != null) {
                roomName = booking.getScheduledClass().getRoom().getRoomName();
            }
        }

        return new BookingResponse(
                booking.getId(),
                MemberDTO.fromEntity(booking.getMember()),
                null,
                booking.getStatus(),
                classStartTime,
                className,
                trainerName,
                roomName
        );
    }
}
