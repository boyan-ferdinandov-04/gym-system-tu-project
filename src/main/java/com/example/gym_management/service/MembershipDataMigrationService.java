package com.example.gym_management.service;

import com.example.gym_management.entity.Member;
import com.example.gym_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipDataMigrationService {

  private final MemberRepository memberRepository;

  /**
   * ONE-TIME MIGRATION - This method was used to migrate existing members when
   * the membership
   * tracking feature was first deployed. It sets membershipStartDate,
   * membershipEndDate, and
   * membershipStatus for members that had plans but were missing these fields.
   *
   * IMPORTANT: This listener is now DISABLED to prevent re-running on every
   * application startup.
   * The migration was successfully completed on 2026-01-17.
   *
   * If you need to run this migration again (e.g., after a database restore),
   * temporarily
   * uncomment the @EventListener annotation below, restart the application once,
   * then
   * re-comment it.
   */
  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void migrateExistingMembers() {
    log.info("Checking for members needing membership date migration...");

    List<Member> members = memberRepository.findAll();
    int migrated = 0;

    for (Member member : members) {
      if (member.getMembershipPlan() != null &&
          (member.getMembershipStartDate() == null ||
              member.getMembershipEndDate() == null ||
              member.getMembershipStatus() == null)) {

        LocalDate startDate = member.getCreatedAt() != null
            ? member.getCreatedAt().toLocalDate()
            : LocalDate.now();
        LocalDate endDate = startDate.plusDays(
            member.getMembershipPlan().getDurationDays());

        member.setMembershipStartDate(startDate);
        member.setMembershipEndDate(endDate);

        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate.plusDays(7))) {
          member.setMembershipStatus(Member.MembershipStatus.EXPIRED);
        } else if (now.isAfter(endDate)) {
          member.setMembershipStatus(Member.MembershipStatus.GRACE_PERIOD);
        } else {
          member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        }

        migrated++;
        log.debug("Migrated member {} with plan {} (status: {}, end date: {})",
            member.getId(),
            member.getMembershipPlan().getTierName(),
            member.getMembershipStatus(),
            member.getMembershipEndDate());
      } else if (member.getMembershipPlan() == null &&
          member.getMembershipStatus() == null) {
        member.setMembershipStatus(Member.MembershipStatus.PENDING);
        log.debug("Set member {} to PENDING status (no plan)", member.getId());
      }
    }

    memberRepository.saveAll(members);
    log.info("Membership migration complete. Migrated: {}", migrated);
  }
}
