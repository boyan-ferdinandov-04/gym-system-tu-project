package com.example.gym_management.repository;

import com.example.gym_management.entity.Waitlist;
import com.example.gym_management.entity.Waitlist.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    @Query("SELECT w FROM Waitlist w WHERE w.scheduledClass.id = :classId " +
           "AND w.status = 'WAITING' ORDER BY w.joinedAt ASC, w.id ASC")
    List<Waitlist> findActiveWaitlistByScheduledClassId(@Param("classId") Long classId);

    @Query("SELECT w FROM Waitlist w WHERE w.scheduledClass.id = :classId " +
           "AND w.status = 'WAITING' ORDER BY w.joinedAt ASC, w.id ASC")
    Optional<Waitlist> findFirstInWaitlist(@Param("classId") Long classId);

    boolean existsByMemberIdAndScheduledClassIdAndStatus(
        Long memberId, Long scheduledClassId, WaitlistStatus status);

    Optional<Waitlist> findByMemberIdAndScheduledClassIdAndStatus(
        Long memberId, Long scheduledClassId, WaitlistStatus status);

    List<Waitlist> findByMemberIdAndStatus(Long memberId, WaitlistStatus status);

    @Query("SELECT COUNT(w) FROM Waitlist w WHERE w.scheduledClass.id = :classId " +
           "AND w.status = 'WAITING'")
    Long countActiveWaitlistByScheduledClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(w) FROM Waitlist w WHERE w.scheduledClass.id = :classId " +
           "AND w.status = 'WAITING' " +
           "AND (w.joinedAt < :joinedAt OR (w.joinedAt = :joinedAt AND w.id < :id))")
    Long getPositionInWaitlist(
        @Param("classId") Long classId,
        @Param("joinedAt") LocalDateTime joinedAt,
        @Param("id") Long id);

    @Query("SELECT w FROM Waitlist w JOIN FETCH w.scheduledClass sc " +
           "WHERE w.status = 'WAITING' AND sc.startTime < :currentTime")
    List<Waitlist> findExpiredWaitlistEntries(@Param("currentTime") LocalDateTime currentTime);

    List<Waitlist> findByScheduledClassId(Long scheduledClassId);
}
