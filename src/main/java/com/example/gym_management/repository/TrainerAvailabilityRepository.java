package com.example.gym_management.repository;

import com.example.gym_management.entity.TrainerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerAvailabilityRepository extends JpaRepository<TrainerAvailability, Long> {

    List<TrainerAvailability> findByTrainerId(Long trainerId);

    List<TrainerAvailability> findByTrainerIdAndDayOfWeek(Long trainerId, DayOfWeek dayOfWeek);

    @Query("SELECT ta FROM TrainerAvailability ta WHERE ta.trainer.id = :trainerId " +
           "AND ta.dayOfWeek = :dayOfWeek AND ta.startTime <= :time AND ta.endTime > :time")
    Optional<TrainerAvailability> findByTrainerIdAndDayOfWeekAndTimeWithin(
            @Param("trainerId") Long trainerId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);

    @Query("SELECT ta FROM TrainerAvailability ta WHERE ta.dayOfWeek = :dayOfWeek " +
           "AND ta.startTime <= :time AND ta.endTime > :time")
    List<TrainerAvailability> findAvailableTrainersByDayAndTime(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);

    @Query("SELECT ta FROM TrainerAvailability ta WHERE ta.trainer.id = :trainerId " +
           "AND ta.dayOfWeek = :dayOfWeek " +
           "AND ((ta.startTime < :endTime AND ta.endTime > :startTime))")
    List<TrainerAvailability> findOverlappingAvailability(
            @Param("trainerId") Long trainerId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT DISTINCT ta.trainer.id FROM TrainerAvailability ta WHERE ta.dayOfWeek = :dayOfWeek " +
           "AND ta.startTime <= :time AND ta.endTime > :time")
    List<Long> findAvailableTrainerIdsByDayAndTime(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);

    void deleteByTrainerId(Long trainerId);
}
