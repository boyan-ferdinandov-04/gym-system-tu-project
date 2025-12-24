package com.example.gym_management.dto;

import com.example.gym_management.entity.ClassType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTypeDTO {

    private Long id;
    private String name;
    private String description;

    public static ClassTypeDTO fromEntity(ClassType classType) {
        if (classType == null) {
            return null;
        }
        return new ClassTypeDTO(
                classType.getId(),
                classType.getName(),
                classType.getDescription()
        );
    }
}
