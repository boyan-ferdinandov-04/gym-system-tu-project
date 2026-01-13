package com.example.gym_management.repository;

import com.example.gym_management.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t WHERE LOWER(t.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Trainer> searchByName(@Param("name") String name);

    @Query("SELECT t FROM Trainer t WHERE LOWER(t.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))")
    List<Trainer> findBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.scheduledClasses WHERE t.id = :id")
    Optional<Trainer> findByIdWithScheduledClasses(@Param("id") Long id);
}
