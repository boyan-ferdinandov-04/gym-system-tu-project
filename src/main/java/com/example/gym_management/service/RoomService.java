package com.example.gym_management.service;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.Room;
import com.example.gym_management.mapper.RoomMapper;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.RoomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class RoomService {

    private final RoomRepository roomRepository;
    private final GymRepository gymRepository;
    private final RoomMapper roomMapper;

    @Transactional
    public RoomDTO createRoom(@Valid RoomRequest request) {
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + request.getGymId()));

        if (roomRepository.existsByGymIdAndRoomName(request.getGymId(), request.getRoomName())) {
            throw new IllegalStateException("A room with name '" + request.getRoomName() + "' already exists in this gym");
        }

        Room room = roomMapper.toEntityWithGym(request, gym);
        Room saved = roomRepository.save(room);
        return roomMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
        return roomMapper.toDto(room);
    }

    @Transactional(readOnly = true)
    public RoomDTO getRoomByIdAndGymId(Long roomId, Long gymId) {
        Room room = roomRepository.findByIdAndGymId(roomId, gymId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId + " in gym: " + gymId));
        return roomMapper.toDto(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomMapper.toDtoList(roomRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByGymId(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("Gym not found with id: " + gymId);
        }
        return roomMapper.toDtoList(roomRepository.findByGymId(gymId));
    }

    @Transactional
    public RoomDTO updateRoom(Long id, @Valid RoomRequest request) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        if (!existingRoom.getRoomName().equals(request.getRoomName()) &&
                roomRepository.existsByGymIdAndRoomName(request.getGymId(), request.getRoomName())) {
            throw new IllegalStateException("A room with name '" + request.getRoomName() + "' already exists in this gym");
        }

        roomMapper.updateEntity(request, existingRoom);

        Room updated = roomRepository.save(existingRoom);
        return roomMapper.toDto(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room existingRoom = roomRepository.findByIdWithScheduledClasses(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        if (existingRoom.getScheduledClasses() != null && !existingRoom.getScheduledClasses().isEmpty()) {
            throw new IllegalStateException("Cannot delete room with scheduled classes. " +
                    "Please remove or reschedule classes first.");
        }

        roomRepository.delete(existingRoom);
    }
}
