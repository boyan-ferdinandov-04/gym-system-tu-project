package com.example.gym_management.service;

import com.example.gym_management.config.MembershipProperties;
import com.example.gym_management.entity.Member;
import com.example.gym_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipExpirationScheduler {

    private final MemberRepository memberRepository;
    private final MembershipProperties membershipProperties;

    @Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
    @Transactional
    public void checkAndUpdateMembershipStatuses() {
        log.info("Starting membership expiration check...");

        int movedToGracePeriod = updateActiveToGracePeriod();
        int movedToExpired = updateGracePeriodToExpired();

        log.info("Membership expiration check complete. " +
                 "Moved to GRACE_PERIOD: {}, Moved to EXPIRED: {}",
                 movedToGracePeriod, movedToExpired);
    }

    private int updateActiveToGracePeriod() {
        LocalDate today = LocalDate.now();
        List<Member> expiredActive = memberRepository.findExpiredActive(today);

        for (Member member : expiredActive) {
            member.setMembershipStatus(Member.MembershipStatus.GRACE_PERIOD);
            log.debug("Moving member {} to GRACE_PERIOD (expired on {})",
                     member.getId(), member.getMembershipEndDate());
        }

        memberRepository.saveAll(expiredActive);
        return expiredActive.size();
    }

    private int updateGracePeriodToExpired() {
        LocalDate gracePeriodCutoff = LocalDate.now().minusDays(membershipProperties.getGracePeriodDays());
        List<Member> beyondGrace = memberRepository.findBeyondGracePeriod(gracePeriodCutoff);

        for (Member member : beyondGrace) {
            member.setMembershipStatus(Member.MembershipStatus.EXPIRED);
            log.debug("Moving member {} to EXPIRED (grace period ended)",
                     member.getId());
        }

        memberRepository.saveAll(beyondGrace);
        return beyondGrace.size();
    }
}
