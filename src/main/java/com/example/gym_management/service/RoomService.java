package com.example.gym_management.service;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.entity.Room;
import com.example.gym_management.repository.RoomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomDTO createRoom(@Valid RoomRequest request) {
        Room room = new Room(
                request.getRoomName(),
                request.getCapacity(),
                request.getHasEquipment()
        );
        Room saved = roomRepository.save(room);
        return RoomDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
        return RoomDTO.fromEntity(room);
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDTO updateRoom(Long id, @Valid RoomRequest request) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        existingRoom.setRoomName(request.getRoomName());
        existingRoom.setCapacity(request.getCapacity());
        existingRoom.setHasEquipment(request.getHasEquipment());

        Room updated = roomRepository.save(existingRoom);
        return RoomDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        if (existingRoom.getScheduledClasses() != null && !existingRoom.getScheduledClasses().isEmpty()) {
            throw new IllegalStateException("Cannot delete room with scheduled classes. " +
                    "Please remove or reschedule classes first.");
        }

        roomRepository.delete(existingRoom);
    }
}
