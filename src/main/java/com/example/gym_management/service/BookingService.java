package com.example.gym_management.service;

import com.example.gym_management.dto.BookingDTOs.BookingEligibility;
import com.example.gym_management.dto.BookingDTOs.ClassAvailability;
import com.example.gym_management.dto.BookingRequest;
import com.example.gym_management.dto.BookingResponse;
import com.example.gym_management.entity.Booking;
import com.example.gym_management.entity.Booking.BookingStatus;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.mapper.BookingMapper;
import com.example.gym_management.repository.BookingRepository;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class BookingService {

  private final BookingRepository bookingRepository;
  private final MemberRepository memberRepository;
  private final ScheduledClassRepository scheduledClassRepository;
  private final BookingMapper bookingMapper;
  private final WaitlistService waitlistService;

  @Transactional
  public BookingResponse createBooking(@Valid BookingRequest request) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Member not found with id: " + request.getMemberId()));

    ScheduledClass scheduledClass = scheduledClassRepository.findById(request.getScheduledClassId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + request.getScheduledClassId()));

    validateBookingEligibility(member, scheduledClass);

    Booking booking = bookingMapper.toEntity(member, scheduledClass);
    Booking savedBooking = bookingRepository.save(booking);

    return bookingMapper.toResponse(savedBooking);
  }

  private void validateBookingEligibility(Member member, ScheduledClass scheduledClass) {
    if (scheduledClass.getStartTime().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException(
          "Cannot book a class that has already started or passed. " +
              "Class start time: " + scheduledClass.getStartTime());
    }

    if (member.getMembershipPlan() == null) {
      throw new IllegalStateException(
          "Member '" + member.getFirstName() + " " + member.getLastName() +
              "' does not have an active membership plan. " +
              "Please assign a membership plan before booking classes.");
    }

    if (bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(
        member.getId(), scheduledClass.getId(), BookingStatus.ENROLLED)) {
      throw new IllegalStateException(
          "Member is already enrolled in this class. " +
              "Cannot create duplicate booking.");
    }

    Long currentEnrollments = bookingRepository.countEnrolledByScheduledClassId(scheduledClass.getId());
    Integer roomCapacity = scheduledClass.getRoom().getCapacity();

    if (currentEnrollments >= roomCapacity) {
      throw new IllegalStateException(
          "Class is fully booked. Current enrollments: " + currentEnrollments +
              ", Room capacity: " + roomCapacity +
              ". Please try another class or check back later for cancellations.");
    }
  }

  @Transactional(readOnly = true)
  public BookingResponse getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
    return bookingMapper.toResponse(booking);
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getAllBookings() {
    return bookingMapper.toResponseList(bookingRepository.findAll());
  }

  @Transactional
  public BookingResponse cancelBooking(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new IllegalStateException("Booking is already cancelled.");
    }

    LocalDateTime cancellationDeadline = booking.getScheduledClass().getStartTime().minusHours(1);
    if (LocalDateTime.now().isAfter(cancellationDeadline)) {
      throw new IllegalStateException(
          "Cannot cancel booking within 1 hour of class start time. " +
              "Cancellation deadline was: " + cancellationDeadline);
    }

    booking.setStatus(BookingStatus.CANCELLED);
    Booking updatedBooking = bookingRepository.save(booking);

    Long scheduledClassId = booking.getScheduledClass().getId();
    waitlistService.promoteFromWaitlist(scheduledClassId);

    return bookingMapper.toResponse(updatedBooking);
  }

  @Transactional
  public BookingResponse reEnrollBooking(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

    if (booking.getStatus() == BookingStatus.ENROLLED) {
      throw new IllegalStateException("Booking is already enrolled.");
    }

    validateBookingEligibility(booking.getMember(), booking.getScheduledClass());

    booking.setStatus(BookingStatus.ENROLLED);
    Booking updatedBooking = bookingRepository.save(booking);

    return bookingMapper.toResponse(updatedBooking);
  }

  @Transactional
  public void deleteBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));

    if (booking.getStatus() == BookingStatus.ENROLLED) {
      throw new IllegalStateException(
          "Cannot delete an active booking. Please cancel the booking first.");
    }

    bookingRepository.delete(booking);
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getBookingsByMember(Long memberId) {
    memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

    return bookingRepository.findByMemberId(memberId)
        .stream()
        .map(bookingMapper::toCompactResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getActiveBookingsByMember(Long memberId) {
    memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

    return bookingRepository.findByMemberIdAndStatus(memberId, BookingStatus.ENROLLED)
        .stream()
        .map(bookingMapper::toCompactResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getUpcomingBookingsByMember(Long memberId) {
    memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

    return bookingRepository.findUpcomingBookingsByMemberId(memberId, LocalDateTime.now())
        .stream()
        .map(bookingMapper::toCompactResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getPastBookingsByMember(Long memberId) {
    memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

    return bookingRepository.findPastBookingsByMemberId(memberId, LocalDateTime.now())
        .stream()
        .map(bookingMapper::toCompactResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getBookingsByScheduledClass(Long scheduledClassId) {
    scheduledClassRepository.findById(scheduledClassId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + scheduledClassId));

    return bookingMapper.toResponseList(bookingRepository.findByScheduledClassId(scheduledClassId));
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getBookingsByStatus(BookingStatus status) {
    return bookingMapper.toResponseList(bookingRepository.findByStatus(status));
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date are required");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }

    return bookingMapper.toResponseList(bookingRepository.findBookingsByDateRange(startDate, endDate));
  }

  @Transactional(readOnly = true)
  public ClassAvailability getClassAvailability(Long scheduledClassId) {
    ScheduledClass scheduledClass = scheduledClassRepository.findById(scheduledClassId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + scheduledClassId));

    Long currentEnrollments = bookingRepository.countEnrolledByScheduledClassId(scheduledClassId);
    Integer roomCapacity = scheduledClass.getRoom().getCapacity();
    Integer availableSpots = roomCapacity - currentEnrollments.intValue();
    boolean isAvailable = availableSpots > 0 && scheduledClass.getStartTime().isAfter(LocalDateTime.now());

    return new ClassAvailability(
        scheduledClassId,
        scheduledClass.getClassType().getName(),
        scheduledClass.getStartTime(),
        roomCapacity,
        currentEnrollments.intValue(),
        availableSpots,
        isAvailable);
  }

  @Transactional(readOnly = true)
  public BookingEligibility checkBookingEligibility(Long memberId, Long scheduledClassId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

    ScheduledClass scheduledClass = scheduledClassRepository.findById(scheduledClassId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + scheduledClassId));

    StringBuilder reason = new StringBuilder();
    boolean eligible = true;

    if (scheduledClass.getStartTime().isBefore(LocalDateTime.now())) {
      eligible = false;
      reason.append("Class has already started. ");
    }

    if (member.getMembershipPlan() == null) {
      eligible = false;
      reason.append("No active membership plan. ");
    }

    if (bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(
        memberId, scheduledClassId, BookingStatus.ENROLLED)) {
      eligible = false;
      reason.append("Already enrolled in this class. ");
    }

    Long currentEnrollments = bookingRepository.countEnrolledByScheduledClassId(scheduledClassId);
    Integer roomCapacity = scheduledClass.getRoom().getCapacity();
    if (currentEnrollments >= roomCapacity) {
      eligible = false;
      reason.append("Class is fully booked. ");
    }

    return new BookingEligibility(
        memberId,
        scheduledClassId,
        eligible,
        eligible ? "Member is eligible to book this class" : reason.toString().trim());
  }

  @Transactional
  public int cancelAllBookingsForClass(Long scheduledClassId) {
    scheduledClassRepository.findById(scheduledClassId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + scheduledClassId));

    List<Booking> activeBookings = bookingRepository.findByScheduledClassId(scheduledClassId)
        .stream()
        .filter(b -> b.getStatus() == BookingStatus.ENROLLED)
        .collect(Collectors.toList());

    for (Booking booking : activeBookings) {
      booking.setStatus(BookingStatus.CANCELLED);
    }

    bookingRepository.saveAll(activeBookings);
    return activeBookings.size();
  }
}
