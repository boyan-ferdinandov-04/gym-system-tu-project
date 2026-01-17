package com.example.gym_management.mapper;

import com.example.gym_management.dto.WaitlistResponse;
import com.example.gym_management.entity.Waitlist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WaitlistMapper {

  private final MemberMapper memberMapper;
  private final ScheduledClassMapper scheduledClassMapper;

  public WaitlistResponse toResponse(Waitlist waitlist) {
    if (waitlist == null) {
      return null;
    }

    String className = null;
    LocalDateTime classStartTime = null;

    if (waitlist.getScheduledClass() != null) {
      classStartTime = waitlist.getScheduledClass().getStartTime();

      if (waitlist.getScheduledClass().getClassType() != null) {
        className = waitlist.getScheduledClass().getClassType().getName();
      }
    }

    return new WaitlistResponse(
        waitlist.getId(),
        memberMapper.toSimpleDto(waitlist.getMember()),
        scheduledClassMapper.toResponse(waitlist.getScheduledClass()),
        waitlist.getStatus(),
        waitlist.getJoinedAt(),
        waitlist.getNotifiedAt(),
        classStartTime,
        className
    );
  }

  public List<WaitlistResponse> toResponseList(List<Waitlist> waitlists) {
    return waitlists.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }
}
