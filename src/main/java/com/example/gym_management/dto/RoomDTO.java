package com.example.gym_management.dto;

import com.example.gym_management.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Long id;
    private String roomName;
    private Integer capacity;
    private Boolean hasEquipment;

    public static RoomDTO fromEntity(Room room) {
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
}
