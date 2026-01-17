package com.example.gym_management.repository;

import com.example.gym_management.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findByMembershipPlanId(Long membershipPlanId);

    @Query("SELECT m FROM Member m WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Member> searchByName(@Param("name") String name);

    @Query("SELECT m FROM Member m WHERE m.membershipPlan IS NULL")
    List<Member> findMembersWithoutPlan();

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.bookings WHERE m.id = :id")
    Optional<Member> findByIdWithBookings(@Param("id") Long id);

    List<Member> findByMembershipStatus(Member.MembershipStatus status);

    @Query("SELECT m FROM Member m WHERE m.membershipEndDate IS NOT NULL " +
           "AND m.membershipEndDate BETWEEN :startDate AND :endDate " +
           "AND m.membershipStatus IN ('ACTIVE', 'GRACE_PERIOD')")
    List<Member> findExpiringBetween(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT m FROM Member m WHERE m.membershipEndDate < :date " +
           "AND m.membershipStatus = 'ACTIVE'")
    List<Member> findExpiredActive(@Param("date") LocalDate date);

    @Query("SELECT m FROM Member m WHERE m.membershipEndDate < :date " +
           "AND m.membershipStatus = 'GRACE_PERIOD'")
    List<Member> findBeyondGracePeriod(@Param("date") LocalDate date);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.membershipStatus = 'ACTIVE'")
    Long countActiveMembers();

    @Query("SELECT m FROM Member m WHERE m.membershipPlan IS NOT NULL " +
           "AND m.membershipEndDate IS NULL")
    List<Member> findWithPlanButNoEndDate();
}
