package com.example.gym_management.repository;

import com.example.gym_management.entity.TrainerTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerTimeOffRepository extends JpaRepository<TrainerTimeOff, Long> {

    List<TrainerTimeOff> findByTrainerId(Long trainerId);

    Optional<TrainerTimeOff> findByTrainerIdAndDate(Long trainerId, LocalDate date);

    @Query("SELECT tto FROM TrainerTimeOff tto WHERE tto.trainer.id = :trainerId " +
           "AND tto.date BETWEEN :startDate AND :endDate")
    List<TrainerTimeOff> findByTrainerIdAndDateRange(
            @Param("trainerId") Long trainerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT tto FROM TrainerTimeOff tto WHERE tto.date = :date")
    List<TrainerTimeOff> findByDate(@Param("date") LocalDate date);

    @Query("SELECT tto.trainer.id FROM TrainerTimeOff tto WHERE tto.date = :date")
    List<Long> findTrainerIdsWithTimeOffOnDate(@Param("date") LocalDate date);

    boolean existsByTrainerIdAndDate(Long trainerId, LocalDate date);

    @Query("SELECT tto FROM TrainerTimeOff tto WHERE tto.trainer.id = :trainerId " +
           "AND tto.date >= :fromDate ORDER BY tto.date ASC")
    List<TrainerTimeOff> findUpcomingByTrainerId(
            @Param("trainerId") Long trainerId,
            @Param("fromDate") LocalDate fromDate);

    void deleteByTrainerId(Long trainerId);
}
