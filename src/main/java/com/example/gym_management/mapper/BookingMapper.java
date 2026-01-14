package com.example.gym_management.mapper;

import com.example.gym_management.dto.BookingRequest;
import com.example.gym_management.dto.BookingResponse;
import com.example.gym_management.entity.Booking;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

  private final MemberRepository memberRepository;
  private final ScheduledClassRepository scheduledClassRepository;
  private final MemberMapper memberMapper;
  private final ScheduledClassMapper scheduledClassMapper;

  public BookingResponse toResponse(Booking booking) {
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
        memberMapper.toSimpleDto(booking.getMember()),
        scheduledClassMapper.toResponse(booking.getScheduledClass()),
        booking.getStatus(),
        classStartTime,
        className,
        trainerName,
        roomName);
  }

  public BookingResponse toCompactResponse(Booking booking) {
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
        memberMapper.toSimpleDto(booking.getMember()),
        null,
        booking.getStatus(),
        classStartTime,
        className,
        trainerName,
        roomName);
  }

  public List<BookingResponse> toResponseList(List<Booking> bookings) {
    return bookings.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public Booking toEntity(BookingRequest request) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Member not found with id: " + request.getMemberId()));

    ScheduledClass scheduledClass = scheduledClassRepository.findById(request.getScheduledClassId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + request.getScheduledClassId()));

    return new Booking(
        member,
        scheduledClass,
        Booking.BookingStatus.ENROLLED);
  }

  public void updateEntity(BookingRequest request, Booking booking) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Member not found with id: " + request.getMemberId()));

    ScheduledClass scheduledClass = scheduledClassRepository.findById(request.getScheduledClassId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + request.getScheduledClassId()));

    booking.setMember(member);
    booking.setScheduledClass(scheduledClass);
  }
}
