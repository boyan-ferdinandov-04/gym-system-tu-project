package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Room details")
public class RoomDTO {

    private Long id;
    private Long gymId;
    private String roomName;

    @Schema(description = "Maximum capacity")
    private Integer capacity;

    @Schema(description = "Has fitness equipment")
    private Boolean hasEquipment;
}
