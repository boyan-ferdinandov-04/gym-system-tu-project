package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {

    private Long id;
    private GymDTO gym;
    private String firstName;
    private String lastName;
    private Integer scheduledClassCount;
    private List<ClassTypeDTO> classTypes;
}
