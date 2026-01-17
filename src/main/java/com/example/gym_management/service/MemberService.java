package com.example.gym_management.service;

import com.example.gym_management.config.MembershipProperties;
import com.example.gym_management.dto.MemberRequest;
import com.example.gym_management.dto.MemberResponse;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.mapper.MemberMapper;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.MembershipPlanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberMapper memberMapper;
    private final MembershipProperties membershipProperties;

    @Transactional
    public MemberResponse createMember(@Valid MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("A member with email '" + request.getEmail() + "' already exists");
        }

        Member member = memberMapper.toEntity(request);
        Member savedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findByIdWithBookings(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
        return memberMapper.toResponse(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberMapper.toResponseListWithoutBookingCount(memberRepository.findAll());
    }

    @Transactional
    public MemberResponse updateMember(Long id, @Valid MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (!member.getEmail().equals(request.getEmail()) &&
                memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("A member with email '" + request.getEmail() + "' already exists");
        }

        memberMapper.updateEntity(request, member);

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findByIdWithBookings(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if (member.getBookings() != null && !member.getBookings().isEmpty()) {
            long activeBookings = member.getBookings().stream()
                    .filter(b -> b.getStatus() == com.example.gym_management.entity.Booking.BookingStatus.ENROLLED)
                    .count();
            if (activeBookings > 0) {
                throw new IllegalStateException(
                        "Cannot delete member with " + activeBookings + " active booking(s). " +
                                "Please cancel all bookings first.");
            }
        }

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be empty");
        }
        return memberMapper.toResponseListWithoutBookingCount(memberRepository.searchByName(name.trim()));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByMembershipPlan(Long membershipPlanId) {
        membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Membership plan not found with id: " + membershipPlanId));

        return memberMapper.toResponseListWithoutBookingCount(
                memberRepository.findByMembershipPlanId(membershipPlanId));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersWithoutPlan() {
        return memberMapper.toResponseListWithoutBookingCount(memberRepository.findMembersWithoutPlan());
    }

    @Transactional
    public MemberResponse assignMembershipPlan(Long memberId, Long membershipPlanId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        MembershipPlan plan = membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Membership plan not found with id: " + membershipPlanId));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        member.setMembershipPlan(plan);
        member.setMembershipStartDate(startDate);
        member.setMembershipEndDate(endDate);
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse removeMembershipPlan(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        member.setMembershipPlan(null);
        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse renewMembership(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        if (member.getMembershipPlan() == null) {
            throw new IllegalStateException("Member does not have a membership plan to renew");
        }

        if (member.getMembershipEndDate() == null) {
            throw new IllegalStateException("Member does not have a membership end date set");
        }

        LocalDate newEndDate = member.getMembershipEndDate()
                .plusDays(member.getMembershipPlan().getDurationDays());

        member.setMembershipEndDate(newEndDate);
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse extendMembership(Long memberId, Integer additionalDays) {
        if (additionalDays == null || additionalDays <= 0) {
            throw new IllegalArgumentException("Additional days must be positive");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        if (member.getMembershipEndDate() == null) {
            throw new IllegalStateException("Member does not have a membership end date set");
        }

        LocalDate newEndDate = member.getMembershipEndDate().plusDays(additionalDays);
        member.setMembershipEndDate(newEndDate);

        if (member.getMembershipStatus() == Member.MembershipStatus.EXPIRED ||
            member.getMembershipStatus() == Member.MembershipStatus.GRACE_PERIOD) {
            member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        }

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse suspendMembership(Long memberId, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        if (member.getMembershipStatus() == null) {
            throw new IllegalStateException("Member does not have a membership status set");
        }

        if (member.getMembershipStatus() != Member.MembershipStatus.ACTIVE &&
            member.getMembershipStatus() != Member.MembershipStatus.GRACE_PERIOD) {
            throw new IllegalStateException(
                    "Only ACTIVE or GRACE_PERIOD memberships can be suspended. Current status: " +
                    member.getMembershipStatus());
        }

        member.setMembershipStatus(Member.MembershipStatus.SUSPENDED);

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse reactivateMembership(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        if (member.getMembershipStatus() != Member.MembershipStatus.SUSPENDED) {
            throw new IllegalStateException(
                    "Only SUSPENDED memberships can be reactivated. Current status: " +
                    member.getMembershipStatus());
        }

        if (member.getMembershipEndDate() == null) {
            throw new IllegalStateException("Member does not have a membership end date set");
        }

        LocalDate today = LocalDate.now();
        if (today.isAfter(member.getMembershipEndDate().plusDays(membershipProperties.getGracePeriodDays()))) {
            throw new IllegalStateException(
                    "Membership is beyond grace period. Please renew instead of reactivating.");
        }

        if (today.isAfter(member.getMembershipEndDate())) {
            member.setMembershipStatus(Member.MembershipStatus.GRACE_PERIOD);
        } else {
            member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        }

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional
    public MemberResponse cancelMembership(Long memberId, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        member.setMembershipStatus(Member.MembershipStatus.CANCELLED);

        Member updatedMember = memberRepository.save(member);
        return memberMapper.toResponseWithoutBookingCount(updatedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getExpiringMembers(Integer daysAhead) {
        if (daysAhead == null || daysAhead < 0) {
            throw new IllegalArgumentException("Days ahead must be non-negative");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        List<Member> members = memberRepository.findExpiringBetween(startDate, endDate);
        return memberMapper.toResponseListWithoutBookingCount(members);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByStatus(Member.MembershipStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        List<Member> members = memberRepository.findByMembershipStatus(status);
        return memberMapper.toResponseListWithoutBookingCount(members);
    }

    @Transactional(readOnly = true)
    public Long getActiveMembersCount() {
        return memberRepository.countActiveMembers();
    }
}
