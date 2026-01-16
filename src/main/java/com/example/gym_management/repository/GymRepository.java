package com.example.gym_management.repository;

import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    Optional<Gym> findByName(String name);

    boolean existsByName(String name);

    List<Gym> findByStatus(GymStatus status);

    @Query("SELECT g FROM Gym g WHERE g.status = :status ORDER BY g.name")
    List<Gym> findActiveGyms(@Param("status") GymStatus status);

    @Query("SELECT g FROM Gym g LEFT JOIN FETCH g.rooms WHERE g.id = :id")
    Optional<Gym> findByIdWithRooms(@Param("id") Long id);

    @Query("SELECT g FROM Gym g LEFT JOIN FETCH g.trainers WHERE g.id = :id")
    Optional<Gym> findByIdWithTrainers(@Param("id") Long id);

    @Query("SELECT g FROM Gym g LEFT JOIN FETCH g.scheduledClasses WHERE g.id = :id")
    Optional<Gym> findByIdWithScheduledClasses(@Param("id") Long id);
}
