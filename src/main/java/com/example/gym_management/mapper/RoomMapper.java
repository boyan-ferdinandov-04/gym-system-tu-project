package com.example.gym_management.mapper;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.Room;
import com.example.gym_management.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomMapper {

    private final GymRepository gymRepository;

    public RoomDTO toDto(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomDTO(
                room.getId(),
                room.getGym() != null ? room.getGym().getId() : null,
                room.getRoomName(),
                room.getCapacity(),
                room.getHasEquipment()
        );
    }

    public List<RoomDTO> toDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Room toEntity(RoomRequest request) {
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + request.getGymId()));

        Room room = new Room();
        room.setGym(gym);
        room.setRoomName(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setHasEquipment(request.getHasEquipment());
        return room;
    }

    public Room toEntityWithGym(RoomRequest request, Gym gym) {
        Room room = new Room();
        room.setGym(gym);
        room.setRoomName(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setHasEquipment(request.getHasEquipment());
        return room;
    }

    public void updateEntity(RoomRequest request, Room room) {
        if (request.getGymId() != null && !request.getGymId().equals(room.getGym().getId())) {
            Gym gym = gymRepository.findById(request.getGymId())
                    .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + request.getGymId()));
            room.setGym(gym);
        }
        room.setRoomName(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setHasEquipment(request.getHasEquipment());
    }
}
