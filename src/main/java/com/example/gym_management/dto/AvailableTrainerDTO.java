package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTrainerDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalTime availableFrom;
    private LocalTime availableUntil;
    private List<ClassTypeDTO> classTypes;
}
