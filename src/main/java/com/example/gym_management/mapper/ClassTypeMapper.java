package com.example.gym_management.mapper;

import com.example.gym_management.dto.ClassTypeDTO;
import com.example.gym_management.dto.ClassTypeRequest;
import com.example.gym_management.dto.ClassTypeResponse;
import com.example.gym_management.entity.ClassType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassTypeMapper {

  public ClassTypeDTO toSimpleDto(ClassType classType) {
    if (classType == null) {
      return null;
    }
    return new ClassTypeDTO(
        classType.getId(),
        classType.getName(),
        classType.getDescription());
  }

  public ClassTypeResponse toResponse(ClassType classType) {
    if (classType == null) {
      return null;
    }
    return new ClassTypeResponse(
        classType.getId(),
        classType.getName(),
        classType.getDescription(),
        calculateScheduledClassCount(classType));
  }

  public ClassTypeResponse toResponseWithoutCount(ClassType classType) {
    if (classType == null) {
      return null;
    }
    return new ClassTypeResponse(
        classType.getId(),
        classType.getName(),
        classType.getDescription(),
        null);
  }

  public List<ClassTypeResponse> toResponseListWithoutCount(List<ClassType> classTypes) {
    return classTypes.stream()
        .map(this::toResponseWithoutCount)
        .collect(Collectors.toList());
  }

  public ClassType toEntity(ClassTypeRequest request) {
    return new ClassType(
        request.getName(),
        request.getDescription());
  }

  public void updateEntity(ClassTypeRequest request, ClassType classType) {
    classType.setName(request.getName());
    classType.setDescription(request.getDescription());
  }

  private Integer calculateScheduledClassCount(ClassType classType) {
    return classType.getScheduledClasses() != null
        ? classType.getScheduledClasses().size()
        : 0;
  }
}
