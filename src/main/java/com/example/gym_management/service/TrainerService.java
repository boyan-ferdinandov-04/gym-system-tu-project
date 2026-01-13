package com.example.gym_management.service;

import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.repository.TrainerRepository;
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
public class TrainerService {

  private final TrainerRepository trainerRepository;

  @Transactional
  public TrainerResponse createTrainer(@Valid TrainerRequest request) {
    Trainer trainer = new Trainer(
        request.getFirstName(),
        request.getLastName(),
        request.getSpecialization());
    Trainer saved = trainerRepository.save(trainer);
    return TrainerResponse.fromEntityWithoutCount(saved);
  }

  @Transactional(readOnly = true)
  public TrainerResponse getTrainerById(Long id) {
    Trainer trainer = trainerRepository.findByIdWithScheduledClasses(id)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + id));
    return TrainerResponse.fromEntity(trainer);
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

  @Transactional(readOnly = true)
  public List<TrainerResponse> getTrainersBySpecialization(String specialization) {
    if (specialization == null || specialization.trim().isEmpty()) {
      throw new IllegalArgumentException("Specialization cannot be empty");
    }
    return trainerRepository.findBySpecialization(specialization)
        .stream()
        .map(TrainerResponse::fromEntityWithoutCount)
        .collect(Collectors.toList());
  }

  @Transactional
  public TrainerResponse updateTrainer(Long id, @Valid TrainerRequest request) {
    Trainer existingTrainer = trainerRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with id: " + id));

    existingTrainer.setFirstName(request.getFirstName());
    existingTrainer.setLastName(request.getLastName());
    existingTrainer.setSpecialization(request.getSpecialization());

    Trainer updated = trainerRepository.save(existingTrainer);
    return TrainerResponse.fromEntityWithoutCount(updated);
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
}
