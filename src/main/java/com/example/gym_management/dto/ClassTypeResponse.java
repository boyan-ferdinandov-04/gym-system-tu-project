package com.example.gym_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTypeResponse {

    private Long id;
    private String name;
    private String description;
    private Integer scheduledClassCount;
}
