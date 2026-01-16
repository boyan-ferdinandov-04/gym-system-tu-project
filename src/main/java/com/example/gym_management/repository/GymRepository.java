package com.example.gym_management.repository;

import com.example.gym_management.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    Optional<Gym> findByName(String name);

    boolean existsByName(String name);
}
