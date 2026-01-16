package com.example.gym_management.repository;

import com.example.gym_management.entity.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    List<ScheduledClass> findByGymId(Long gymId);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.id = :classId")
    Optional<ScheduledClass> findByIdAndGymId(@Param("classId") Long classId, @Param("gymId") Long gymId);

    List<ScheduledClass> findByTrainerId(Long trainerId);

    List<ScheduledClass> findByRoomId(Long roomId);

    List<ScheduledClass> findByClassTypeId(Long classTypeId);

    List<ScheduledClass> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.startTime >= :startTime")
    List<ScheduledClass> findUpcomingClasses(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.startTime >= :startTime")
    List<ScheduledClass> findUpcomingClassesByGymId(@Param("gymId") Long gymId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.trainer.id = :trainerId AND sc.startTime BETWEEN :startTime AND :endTime")
    List<ScheduledClass> findByTrainerIdAndTimeRange(@Param("trainerId") Long trainerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.room.id = :roomId AND sc.startTime BETWEEN :startTime AND :endTime")
    List<ScheduledClass> findByRoomIdAndTimeRange(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.trainer.id = :trainerId")
    List<ScheduledClass> findByGymIdAndTrainerId(@Param("gymId") Long gymId, @Param("trainerId") Long trainerId);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.room.id = :roomId")
    List<ScheduledClass> findByGymIdAndRoomId(@Param("gymId") Long gymId, @Param("roomId") Long roomId);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.classType.id = :classTypeId")
    List<ScheduledClass> findByGymIdAndClassTypeId(@Param("gymId") Long gymId, @Param("classTypeId") Long classTypeId);

    @Query("SELECT sc FROM ScheduledClass sc WHERE sc.gym.id = :gymId AND sc.startTime BETWEEN :startDate AND :endDate")
    List<ScheduledClass> findByGymIdAndStartTimeBetween(@Param("gymId") Long gymId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
