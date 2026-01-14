package com.example.gym_management.service;

import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.repository.ClassTypeRepository;
import com.example.gym_management.repository.TrainerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class TrainerService {

  private final TrainerRepository trainerRepository;
  private final ClassTypeRepository classTypeRepository;

  @Transactional
  public TrainerResponse createTrainer(@Valid TrainerRequest request) {
    Trainer trainer = new Trainer(
        request.getFirstName(),
        request.getLastName());

    if (request.getClassTypeIds() != null && !request.getClassTypeIds().isEmpty()) {
      Set<ClassType> classTypes = new HashSet<>(classTypeRepository.findAllById(request.getClassTypeIds()));
      if (classTypes.size() != request.getClassTypeIds().size()) {
        throw new IllegalArgumentException("One or more class type IDs are invalid");
      }
      trainer.setClassTypes(classTypes);
    }

    Trainer saved = trainerRepository.save(trainer);
    return TrainerResponse.fromEntityWithClassTypes(saved);
  }

  @Transactional(readOnly = true)
  public TrainerResponse getTrainerById(Long id) {
    Trainer trainer = trainerRepository.findByIdWithClassTypesAndScheduledClasses(id)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + id));
    return TrainerResponse.fromEntityWithClassTypes(trainer);
  }

  @Transactional(readOnly = true)
  public List<TrainerResponse> getAllTrainers() {
    return trainerRepository.findAll()
        .stream()
        .map(TrainerResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TrainerResponse> searchTrainersByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Search name cannot be empty");
    }
    return trainerRepository.searchByName(name)
        .stream()
        .map(TrainerResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }

  @Transactional
  public TrainerResponse updateTrainer(Long id, @Valid TrainerRequest request) {
    Trainer existingTrainer = trainerRepository.findByIdWithClassTypes(id)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + id));

    existingTrainer.setFirstName(request.getFirstName());
    existingTrainer.setLastName(request.getLastName());

    if (request.getClassTypeIds() != null) {
      if (request.getClassTypeIds().isEmpty()) {
        existingTrainer.getClassTypes().clear();
      } else {
        Set<ClassType> classTypes = new HashSet<>(classTypeRepository.findAllById(request.getClassTypeIds()));
        if (classTypes.size() != request.getClassTypeIds().size()) {
          throw new IllegalArgumentException("One or more class type IDs are invalid");
        }
        existingTrainer.setClassTypes(classTypes);
      }
    }

    Trainer updated = trainerRepository.save(existingTrainer);
    return TrainerResponse.fromEntityWithClassTypes(updated);
  }

  @Transactional
  public void deleteTrainer(Long id) {
    Trainer existingTrainer = trainerRepository.findByIdWithScheduledClasses(id)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + id));

    if (existingTrainer.getScheduledClasses() != null && !existingTrainer.getScheduledClasses().isEmpty()) {
      throw new IllegalStateException("Cannot delete trainer with scheduled classes. " +
          "Please remove or reassign classes first.");
    }

    trainerRepository.delete(existingTrainer);
  }

  @Transactional
  public TrainerResponse assignClassTypes(Long trainerId, Set<Long> classTypeIds) {
    if (classTypeIds == null || classTypeIds.isEmpty()) {
      throw new IllegalArgumentException("Class type IDs cannot be empty");
    }

    Trainer trainer = trainerRepository.findByIdWithClassTypes(trainerId)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + trainerId));

    Set<ClassType> classTypes = new HashSet<>(classTypeRepository.findAllById(classTypeIds));
    if (classTypes.size() != classTypeIds.size()) {
      throw new IllegalArgumentException("One or more class type IDs are invalid");
    }

    trainer.getClassTypes().addAll(classTypes);
    Trainer updated = trainerRepository.save(trainer);
    return TrainerResponse.fromEntityWithClassTypes(updated);
  }

  @Transactional
  public TrainerResponse removeClassTypes(Long trainerId, Set<Long> classTypeIds) {
    if (classTypeIds == null || classTypeIds.isEmpty()) {
      throw new IllegalArgumentException("Class type IDs cannot be empty");
    }

    Trainer trainer = trainerRepository.findByIdWithClassTypes(trainerId)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + trainerId));

    trainer.getClassTypes().removeIf(ct -> classTypeIds.contains(ct.getId()));
    Trainer updated = trainerRepository.save(trainer);
    return TrainerResponse.fromEntityWithClassTypes(updated);
  }

  @Transactional(readOnly = true)
  public List<TrainerResponse> getTrainersByClassType(Long classTypeId) {
    if (!classTypeRepository.existsById(classTypeId)) {
      throw new IllegalArgumentException("Class type not found with id: " + classTypeId);
    }
    return trainerRepository.findByClassTypeId(classTypeId)
        .stream()
        .map(TrainerResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }
}
