package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer details")
public class TrainerResponse {

    private Long id;
    private GymDTO gym;
    private String firstName;
    private String lastName;

    @Schema(description = "Number of assigned scheduled classes")
    private Integer scheduledClassCount;

    @Schema(description = "Class types this trainer can teach")
    private List<ClassTypeDTO> classTypes;
}
