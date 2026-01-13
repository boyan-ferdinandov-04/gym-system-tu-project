package com.example.gym_management.dto;

import com.example.gym_management.entity.ClassType;
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

    public static ClassTypeResponse fromEntity(ClassType classType) {
        if (classType == null) {
            return null;
        }
        return new ClassTypeResponse(
                classType.getId(),
                classType.getName(),
                classType.getDescription(),
                classType.getScheduledClasses() != null ? classType.getScheduledClasses().size() : 0
        );
    }

    public static ClassTypeResponse fromEntityWithoutCount(ClassType classType) {
        if (classType == null) {
            return null;
        }
        return new ClassTypeResponse(
                classType.getId(),
                classType.getName(),
                classType.getDescription(),
                null
        );
    }
}
