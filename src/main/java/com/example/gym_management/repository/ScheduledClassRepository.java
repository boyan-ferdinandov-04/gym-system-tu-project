package com.example.gym_management.repository;

import com.example.gym_management.entity.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    List<ScheduledClass> findByTrainerId(Long trainerId);

    List<ScheduledClass> findByRoomId(Long roomId);

    List<ScheduledClass> findByClassTypeId(Long classTypeId);

    List<ScheduledClass> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.startTime >= :startTime")
    List<ScheduledClass> findUpcomingClasses(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.trainer.id = :trainerId AND sc.startTime BETWEEN :startTime AND :endTime")
    List<ScheduledClass> findByTrainerIdAndTimeRange(@Param("trainerId") Long trainerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.room.id = :roomId AND sc.startTime BETWEEN :startTime AND :endTime")
    List<ScheduledClass> findByRoomIdAndTimeRange(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
