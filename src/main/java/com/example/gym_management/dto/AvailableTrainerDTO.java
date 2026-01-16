package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Available trainer with time window")
public class AvailableTrainerDTO {

    private Long id;
    private String firstName;
    private String lastName;

    @Schema(description = "Start of availability window")
    private LocalTime availableFrom;

    @Schema(description = "End of availability window")
    private LocalTime availableUntil;

    private List<ClassTypeDTO> classTypes;
}
