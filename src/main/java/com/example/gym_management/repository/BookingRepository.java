package com.example.gym_management.repository;

import com.example.gym_management.entity.Booking;
import com.example.gym_management.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByMemberId(Long memberId);

    List<Booking> findByScheduledClassId(Long scheduledClassId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByMemberIdAndStatus(Long memberId, BookingStatus status);

    Optional<Booking> findByMemberIdAndScheduledClassId(Long memberId, Long scheduledClassId);

    @Query("SELECT b FROM Booking b WHERE b.member.id = :memberId AND b.scheduledClass.id = :classId AND b.status = :status")
    Optional<Booking> findByMemberIdAndScheduledClassIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("classId") Long classId,
            @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.scheduledClass.id = :classId AND b.status = 'ENROLLED'")
    Long countEnrolledByScheduledClassId(@Param("classId") Long classId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.scheduledClass sc WHERE b.member.id = :memberId AND sc.startTime >= :startTime AND b.status = 'ENROLLED' ORDER BY sc.startTime")
    List<Booking> findUpcomingBookingsByMemberId(@Param("memberId") Long memberId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.scheduledClass sc WHERE b.member.id = :memberId AND sc.startTime < :endTime AND b.status = 'ENROLLED' ORDER BY sc.startTime DESC")
    List<Booking> findPastBookingsByMemberId(@Param("memberId") Long memberId, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.scheduledClass.startTime BETWEEN :startTime AND :endTime")
    List<Booking> findBookingsByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    boolean existsByMemberIdAndScheduledClassIdAndStatus(Long memberId, Long scheduledClassId, BookingStatus status);
}
