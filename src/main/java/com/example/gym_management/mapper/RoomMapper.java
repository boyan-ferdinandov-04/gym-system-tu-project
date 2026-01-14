package com.example.gym_management.mapper;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.entity.Room;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    public RoomDTO toDto(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomDTO(
                room.getId(),
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
        return new Room(
                request.getRoomName(),
                request.getCapacity(),
                request.getHasEquipment()
        );
    }

    public void updateEntity(RoomRequest request, Room room) {
        room.setRoomName(request.getRoomName());
        room.setCapacity(request.getCapacity());
        room.setHasEquipment(request.getHasEquipment());
    }
}
