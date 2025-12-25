package com.example.gym_management.service;

import com.example.gym_management.dto.ScheduledClassRequest;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.Room;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.repository.ClassTypeRepository;
import com.example.gym_management.repository.RoomRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import com.example.gym_management.repository.TrainerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ScheduledClassService {
  private final ScheduledClassRepository scheduledClassRepository;
  private final ClassTypeRepository classTypeRepository;
  private final TrainerRepository trainerRepository;
  private final RoomRepository roomRepository;

  @Transactional
  public ScheduledClassResponse createScheduledClass(@Valid ScheduledClassRequest request) {
    ClassType classType = classTypeRepository.findById(request.getClassTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Class type not found with ID: " + request.getClassTypeId()));

    Trainer trainer = trainerRepository.findById(request.getTrainerId())
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + request.getTrainerId()));

    Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + request.getRoomId()));

    validateSchedulingConflicts(request.getTrainerId(), request.getRoomId(), request.getStartTime(), null);

    ScheduledClass scheduledClass = new ScheduledClass();
    scheduledClass.setClassType(classType);
    scheduledClass.setTrainer(trainer);
    scheduledClass.setRoom(room);
    scheduledClass.setStartTime(request.getStartTime());

    ScheduledClass savedClass = scheduledClassRepository.save(scheduledClass);
    return ScheduledClassResponse.fromEntity(savedClass);
  }

  @Transactional(readOnly = true)
  public ScheduledClassResponse getScheduledClassById(Long id) {
    ScheduledClass scheduledClass = scheduledClassRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Scheduled class not found with ID: " + id));
    return ScheduledClassResponse.fromEntity(scheduledClass);
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getAllScheduledClasses() {
    return scheduledClassRepository.findAll()
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getUpcomingClasses() {
    return scheduledClassRepository.findUpcomingClasses(LocalDateTime.now())
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getClassesByTrainer(Long trainerId) {
    if (!trainerRepository.existsById(trainerId)) {
      throw new IllegalArgumentException("Trainer not found with ID: " + trainerId);
    }
    return scheduledClassRepository.findByTrainerId(trainerId)
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getClassesByRoom(Long roomId) {
    if (!roomRepository.existsById(roomId)) {
      throw new IllegalArgumentException("Room not found with ID: " + roomId);
    }
    return scheduledClassRepository.findByRoomId(roomId)
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getClassesByType(Long classTypeId) {
    if (!classTypeRepository.existsById(classTypeId)) {
      throw new IllegalArgumentException("Class type not found with ID: " + classTypeId);
    }
    return scheduledClassRepository.findByClassTypeId(classTypeId)
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getClassesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }
    return scheduledClassRepository.findByStartTimeBetween(startDate, endDate)
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ScheduledClassResponse> getAvailableClasses() {
    return scheduledClassRepository.findUpcomingClasses(LocalDateTime.now())
        .stream()
        .map(ScheduledClassResponse::fromEntity)
        .filter(response -> response.getAvailableSpots() > 0)
        .collect(Collectors.toList());
  }

  @Transactional
  public ScheduledClassResponse updateScheduledClass(Long id, @Valid ScheduledClassRequest request) {
    ScheduledClass existingClass = scheduledClassRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Scheduled class not found with ID: " + id));

    ClassType classType = classTypeRepository.findById(request.getClassTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Class type not found with ID: " + request.getClassTypeId()));

    Trainer trainer = trainerRepository.findById(request.getTrainerId())
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + request.getTrainerId()));

    Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + request.getRoomId()));

    validateSchedulingConflicts(request.getTrainerId(), request.getRoomId(), request.getStartTime(), id);

    existingClass.setClassType(classType);
    existingClass.setTrainer(trainer);
    existingClass.setRoom(room);
    existingClass.setStartTime(request.getStartTime());

    ScheduledClass updatedClass = scheduledClassRepository.save(existingClass);
    return ScheduledClassResponse.fromEntity(updatedClass);
  }

  @Transactional
  public void deleteScheduledClass(Long id) {
    ScheduledClass scheduledClass = scheduledClassRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Scheduled class not found with ID: " + id));

    if (scheduledClass.getBookings() != null && !scheduledClass.getBookings().isEmpty()) {
      throw new IllegalStateException(
          "Cannot delete scheduled class with existing bookings. Please cancel all bookings first.");
    }

    scheduledClassRepository.delete(scheduledClass);
  }

  private void validateSchedulingConflicts(Long trainerId, Long roomId, LocalDateTime startTime, Long excludeClassId) {
    LocalDateTime endTime = startTime.plusHours(1);

    List<ScheduledClass> trainerConflicts = scheduledClassRepository
        .findByTrainerIdAndTimeRange(trainerId, startTime, endTime);
    if (excludeClassId != null) {
      trainerConflicts = trainerConflicts.stream()
          .filter(sc -> !sc.getId().equals(excludeClassId))
          .collect(Collectors.toList());
    }
    if (!trainerConflicts.isEmpty()) {
      throw new IllegalStateException("Trainer is already scheduled for another class at this time");
    }

    List<ScheduledClass> roomConflicts = scheduledClassRepository
        .findByRoomIdAndTimeRange(roomId, startTime, endTime);
    if (excludeClassId != null) {
      roomConflicts = roomConflicts.stream()
          .filter(sc -> !sc.getId().equals(excludeClassId))
          .collect(Collectors.toList());
    }
    if (!roomConflicts.isEmpty()) {
      throw new IllegalStateException("Room is already occupied at this time");
    }
  }
}
