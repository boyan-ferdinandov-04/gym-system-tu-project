package com.example.gym_management.repository;

import com.example.gym_management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByGymId(Long gymId);

    @Query("SELECT r FROM Room r WHERE r.gym.id = :gymId AND r.id = :roomId")
    Optional<Room> findByIdAndGymId(@Param("roomId") Long roomId, @Param("gymId") Long gymId);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.scheduledClasses WHERE r.id = :id")
    Optional<Room> findByIdWithScheduledClasses(@Param("id") Long id);

    boolean existsByGymIdAndRoomName(Long gymId, String roomName);
}
