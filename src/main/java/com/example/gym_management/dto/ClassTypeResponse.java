package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Class type details")
public class ClassTypeResponse {

    private Long id;
    private String name;
    private String description;

    @Schema(description = "Number of scheduled classes of this type")
    private Integer scheduledClassCount;
}
