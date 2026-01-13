package com.example.gym_management.repository;

import com.example.gym_management.entity.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {

    @Query("SELECT ct FROM ClassType ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ClassType> searchByName(@Param("name") String name);

    Optional<ClassType> findByName(String name);

    @Query("SELECT ct FROM ClassType ct LEFT JOIN FETCH ct.scheduledClasses WHERE ct.id = :id")
    Optional<ClassType> findByIdWithScheduledClasses(@Param("id") Long id);

    boolean existsByName(String name);
}
