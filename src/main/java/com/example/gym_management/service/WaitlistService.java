package com.example.gym_management.service;

import com.example.gym_management.dto.WaitlistPositionResponse;
import com.example.gym_management.dto.WaitlistRequest;
import com.example.gym_management.dto.WaitlistResponse;
import com.example.gym_management.entity.Booking;
import com.example.gym_management.entity.Booking.BookingStatus;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.entity.Waitlist;
import com.example.gym_management.entity.Waitlist.WaitlistStatus;
import com.example.gym_management.mapper.WaitlistMapper;
import com.example.gym_management.repository.BookingRepository;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import com.example.gym_management.repository.WaitlistRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class WaitlistService {

  private final WaitlistRepository waitlistRepository;
  private final MemberRepository memberRepository;
  private final ScheduledClassRepository scheduledClassRepository;
  private final BookingRepository bookingRepository;
  private final WaitlistMapper waitlistMapper;

  @Transactional
  public WaitlistResponse addToWaitlist(@Valid WaitlistRequest request) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Member not found with id: " + request.getMemberId()));

    ScheduledClass scheduledClass = scheduledClassRepository.findById(request.getScheduledClassId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + request.getScheduledClassId()));

    validateWaitlistEligibility(member, scheduledClass);

    Waitlist waitlist = new Waitlist(member, scheduledClass);
    Waitlist saved = waitlistRepository.save(waitlist);

    return waitlistMapper.toResponse(saved);
  }

  private void validateWaitlistEligibility(Member member, ScheduledClass scheduledClass) {
    if (scheduledClass.getStartTime().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException(
          "Cannot join waitlist for a class that has already started. " +
              "Class start time: " + scheduledClass.getStartTime());
    }

    if (member.getMembershipPlan() == null) {
      throw new IllegalStateException(
          "Member does not have an active membership plan. " +
              "Please assign a membership plan before joining waitlist.");
    }

    if (bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(
        member.getId(), scheduledClass.getId(), BookingStatus.ENROLLED)) {
      throw new IllegalStateException(
          "Member is already enrolled in this class. Cannot join waitlist.");
    }

    if (waitlistRepository.existsByMemberIdAndScheduledClassIdAndStatus(
        member.getId(), scheduledClass.getId(), WaitlistStatus.WAITING)) {
      throw new IllegalStateException(
          "Member is already on the waitlist for this class.");
    }

    Long currentEnrollments = bookingRepository.countEnrolledByScheduledClassId(
        scheduledClass.getId());
    Integer roomCapacity = scheduledClass.getRoom().getCapacity();

    if (currentEnrollments < roomCapacity) {
      throw new IllegalStateException(
          "Class is not full. Please book directly instead of joining waitlist. " +
              "Available spots: " + (roomCapacity - currentEnrollments));
    }
  }

  @Transactional
  public WaitlistResponse removeFromWaitlist(Long waitlistId) {
    Waitlist waitlist = waitlistRepository.findById(waitlistId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Waitlist entry not found with id: " + waitlistId));

    if (waitlist.getStatus() != WaitlistStatus.WAITING) {
      throw new IllegalStateException(
          "Cannot remove from waitlist. Current status: " + waitlist.getStatus());
    }

    waitlist.setStatus(WaitlistStatus.REMOVED);
    Waitlist updated = waitlistRepository.save(waitlist);

    return waitlistMapper.toResponse(updated);
  }

  @Transactional(readOnly = true)
  public WaitlistPositionResponse getWaitlistPosition(Long waitlistId) {
    Waitlist waitlist = waitlistRepository.findById(waitlistId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Waitlist entry not found with id: " + waitlistId));

    if (waitlist.getStatus() != WaitlistStatus.WAITING) {
      throw new IllegalStateException(
          "Waitlist entry is not active. Status: " + waitlist.getStatus());
    }

    Long position = waitlistRepository.getPositionInWaitlist(
        waitlist.getScheduledClass().getId(),
        waitlist.getJoinedAt(),
        waitlist.getId());

    Long totalWaiting = waitlistRepository.countActiveWaitlistByScheduledClassId(
        waitlist.getScheduledClass().getId());

    return new WaitlistPositionResponse(
        waitlistId,
        waitlist.getMember().getId(),
        waitlist.getScheduledClass().getId(),
        position + 1,
        totalWaiting);
  }

  @Transactional(readOnly = true)
  public List<WaitlistResponse> getWaitlistByClass(Long scheduledClassId) {
    scheduledClassRepository.findById(scheduledClassId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Scheduled class not found with id: " + scheduledClassId));

    List<Waitlist> waitlists = waitlistRepository
        .findActiveWaitlistByScheduledClassId(scheduledClassId);

    return waitlistMapper.toResponseList(waitlists);
  }

  @Transactional(readOnly = true)
  public List<WaitlistResponse> getMemberWaitlists(Long memberId) {
    memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Member not found with id: " + memberId));

    List<Waitlist> waitlists = waitlistRepository
        .findByMemberIdAndStatus(memberId, WaitlistStatus.WAITING);

    return waitlistMapper.toResponseList(waitlists);
  }

  @Transactional
  public int expireWaitlistEntries() {
    List<Waitlist> expiredEntries = waitlistRepository
        .findExpiredWaitlistEntries(LocalDateTime.now());

    for (Waitlist waitlist : expiredEntries) {
      waitlist.setStatus(WaitlistStatus.EXPIRED);
    }

    waitlistRepository.saveAll(expiredEntries);
    return expiredEntries.size();
  }

  @Transactional(readOnly = true)
  public Long getWaitlistSize(Long scheduledClassId) {
    return waitlistRepository.countActiveWaitlistByScheduledClassId(scheduledClassId);
  }

  @Transactional
  public Optional<Booking> promoteFromWaitlist(Long scheduledClassId) {
    Optional<Waitlist> firstInQueue = waitlistRepository
        .findFirstInWaitlist(scheduledClassId);

    if (firstInQueue.isEmpty()) {
      return Optional.empty();
    }

    Waitlist waitlist = firstInQueue.get();

    try {
      Booking booking = new Booking(
          waitlist.getMember(),
          waitlist.getScheduledClass(),
          BookingStatus.ENROLLED);

      Booking savedBooking = bookingRepository.save(booking);

      waitlist.setStatus(WaitlistStatus.PROMOTED);
      waitlist.setNotifiedAt(LocalDateTime.now());
      waitlistRepository.save(waitlist);

      return Optional.of(savedBooking);

    } catch (OptimisticLockException e) {
      return promoteFromWaitlist(scheduledClassId);
    } catch (Exception e) {
      waitlist.setStatus(WaitlistStatus.EXPIRED);
      waitlistRepository.save(waitlist);
      return promoteFromWaitlist(scheduledClassId);
    }
  }
}
