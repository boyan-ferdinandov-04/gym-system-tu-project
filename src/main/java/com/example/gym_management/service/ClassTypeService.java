package com.example.gym_management.service;

import com.example.gym_management.dto.ClassTypeRequest;
import com.example.gym_management.dto.ClassTypeResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.repository.ClassTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ClassTypeService {

  private final ClassTypeRepository classTypeRepository;

  @Transactional
  public ClassTypeResponse createClassType(@Valid ClassTypeRequest request) {
    if (classTypeRepository.existsByName(request.getName())) {
      throw new IllegalStateException("Class type with name '" + request.getName() + "' already exists");
    }

    ClassType classType = new ClassType(
        request.getName(),
        request.getDescription());
    ClassType saved = classTypeRepository.save(classType);
    return ClassTypeResponse.fromEntityWithoutCount(saved);
  }

  @Transactional(readOnly = true)
  public ClassTypeResponse getClassTypeById(Long id) {
    ClassType classType = classTypeRepository.findByIdWithScheduledClasses(id)
        .orElseThrow(() -> new IllegalArgumentException("Class type not found with id: " + id));
    return ClassTypeResponse.fromEntity(classType);
  }

  @Transactional(readOnly = true)
  public List<ClassTypeResponse> getAllClassTypes() {
    return classTypeRepository.findAll()
        .stream()
        .map(ClassTypeResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ClassTypeResponse> searchClassTypesByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Search name cannot be empty");
    }
    return classTypeRepository.searchByName(name)
        .stream()
        .map(ClassTypeResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }

  @Transactional
  public ClassTypeResponse updateClassType(Long id, @Valid ClassTypeRequest request) {
    ClassType existingClassType = classTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Class type not found with id: " + id));

    // Check if name is being changed to an existing name
    if (!existingClassType.getName().equals(request.getName()) &&
        classTypeRepository.existsByName(request.getName())) {
      throw new IllegalStateException("Class type with name '" + request.getName() + "' already exists");
    }

    existingClassType.setName(request.getName());
    existingClassType.setDescription(request.getDescription());

    ClassType updated = classTypeRepository.save(existingClassType);
    return ClassTypeResponse.fromEntityWithoutCount(updated);
  }

  @Transactional
  public void deleteClassType(Long id) {
    ClassType existingClassType = classTypeRepository.findByIdWithScheduledClasses(id)
        .orElseThrow(() -> new IllegalArgumentException("Class type not found with id: " + id));

    if (existingClassType.getScheduledClasses() != null && !existingClassType.getScheduledClasses().isEmpty()) {
      throw new IllegalStateException("Cannot delete class type with scheduled classes. " +
          "Please remove or reassign classes first.");
    }

    classTypeRepository.delete(existingClassType);
  }
}
