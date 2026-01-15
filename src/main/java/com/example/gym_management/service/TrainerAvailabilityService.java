package com.example.gym_management.service;

import com.example.gym_management.dto.*;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.entity.TrainerAvailability;
import com.example.gym_management.entity.TrainerTimeOff;
import com.example.gym_management.mapper.TrainerAvailabilityMapper;
import com.example.gym_management.repository.TrainerAvailabilityRepository;
import com.example.gym_management.repository.TrainerRepository;
import com.example.gym_management.repository.TrainerTimeOffRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class TrainerAvailabilityService {

    private final TrainerAvailabilityRepository availabilityRepository;
    private final TrainerTimeOffRepository timeOffRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerAvailabilityMapper availabilityMapper;

    @Transactional
    public TrainerAvailabilityResponse createAvailability(Long trainerId, @Valid TrainerAvailabilityRequest request) {
        validateTrainerExists(trainerId);
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateNoOverlap(trainerId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        TrainerAvailability availability = availabilityMapper.toAvailabilityEntity(request, trainerId);
        TrainerAvailability saved = availabilityRepository.save(availability);
        return availabilityMapper.toAvailabilityResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainerAvailabilityResponse> getTrainerAvailability(Long trainerId) {
        validateTrainerExists(trainerId);
        return availabilityMapper.toAvailabilityResponseList(
                availabilityRepository.findByTrainerId(trainerId));
    }

    @Transactional(readOnly = true)
    public TrainerAvailabilityResponse getAvailabilityById(Long trainerId, Long slotId) {
        TrainerAvailability availability = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability slot not found with ID: " + slotId));

        if (!availability.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("Availability slot does not belong to trainer with ID: " + trainerId);
        }

        return availabilityMapper.toAvailabilityResponse(availability);
    }

    @Transactional
    public TrainerAvailabilityResponse updateAvailability(Long trainerId, Long slotId, @Valid TrainerAvailabilityRequest request) {
        TrainerAvailability availability = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability slot not found with ID: " + slotId));

        if (!availability.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("Availability slot does not belong to trainer with ID: " + trainerId);
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateNoOverlap(trainerId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), slotId);

        availabilityMapper.updateAvailabilityEntity(request, availability);
        TrainerAvailability updated = availabilityRepository.save(availability);
        return availabilityMapper.toAvailabilityResponse(updated);
    }

    @Transactional
    public void deleteAvailability(Long trainerId, Long slotId) {
        TrainerAvailability availability = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Availability slot not found with ID: " + slotId));

        if (!availability.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("Availability slot does not belong to trainer with ID: " + trainerId);
        }

        availabilityRepository.delete(availability);
    }

    @Transactional
    public TrainerTimeOffResponse createTimeOff(Long trainerId, @Valid TrainerTimeOffRequest request) {
        validateTrainerExists(trainerId);

        if (timeOffRepository.existsByTrainerIdAndDate(trainerId, request.getDate())) {
            throw new IllegalStateException("Time off already exists for this date");
        }

        TrainerTimeOff timeOff = availabilityMapper.toTimeOffEntity(request, trainerId);
        TrainerTimeOff saved = timeOffRepository.save(timeOff);
        return availabilityMapper.toTimeOffResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TrainerTimeOffResponse> getTrainerTimeOff(Long trainerId) {
        validateTrainerExists(trainerId);
        return availabilityMapper.toTimeOffResponseList(
                timeOffRepository.findByTrainerId(trainerId));
    }

    @Transactional(readOnly = true)
    public List<TrainerTimeOffResponse> getUpcomingTimeOff(Long trainerId) {
        validateTrainerExists(trainerId);
        return availabilityMapper.toTimeOffResponseList(
                timeOffRepository.findUpcomingByTrainerId(trainerId, LocalDate.now()));
    }

    @Transactional
    public void deleteTimeOff(Long trainerId, Long timeOffId) {
        TrainerTimeOff timeOff = timeOffRepository.findById(timeOffId)
                .orElseThrow(() -> new IllegalArgumentException("Time off not found with ID: " + timeOffId));

        if (!timeOff.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("Time off does not belong to trainer with ID: " + trainerId);
        }

        timeOffRepository.delete(timeOff);
    }

    @Transactional(readOnly = true)
    public boolean isTrainerAvailable(Long trainerId, LocalDateTime dateTime) {
        validateTrainerExists(trainerId);

        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

        if (timeOffRepository.existsByTrainerIdAndDate(trainerId, date)) {
            return false;
        }

        Optional<TrainerAvailability> availability = availabilityRepository
                .findByTrainerIdAndDayOfWeekAndTimeWithin(trainerId, dayOfWeek, time);

        return availability.isPresent();
    }

    @Transactional(readOnly = true)
    public List<AvailableTrainerDTO> findAvailableTrainers(LocalDate date, LocalTime time) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        Set<Long> trainersWithTimeOff = timeOffRepository.findTrainerIdsWithTimeOffOnDate(date)
                .stream()
                .collect(Collectors.toSet());

        List<TrainerAvailability> availabilities = availabilityRepository
                .findAvailableTrainersByDayAndTime(dayOfWeek, time);

        return availabilities.stream()
                .filter(av -> !trainersWithTimeOff.contains(av.getTrainer().getId()))
                .map(av -> {
                    Trainer trainer = trainerRepository.findByIdWithClassTypes(av.getTrainer().getId())
                            .orElse(av.getTrainer());
                    return availabilityMapper.toAvailableTrainerDTO(trainer, av);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void validateTrainerAvailabilityForClass(Long trainerId, LocalDateTime startTime) {
        if (!isTrainerAvailable(trainerId, startTime)) {
            LocalDate date = startTime.toLocalDate();
            LocalTime time = startTime.toLocalTime();

            if (timeOffRepository.existsByTrainerIdAndDate(trainerId, date)) {
                throw new IllegalStateException("Trainer has time off on " + date);
            }

            throw new IllegalStateException(
                    "Trainer is not available on " + startTime.getDayOfWeek() + " at " + time);
        }
    }

    private void validateTrainerExists(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new IllegalArgumentException("Trainer not found with ID: " + trainerId);
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private void validateNoOverlap(Long trainerId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Long excludeId) {
        List<TrainerAvailability> overlapping = availabilityRepository
                .findOverlappingAvailability(trainerId, dayOfWeek, startTime, endTime);

        if (excludeId != null) {
            overlapping = overlapping.stream()
                    .filter(av -> !av.getId().equals(excludeId))
                    .collect(Collectors.toList());
        }

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Availability slot overlaps with existing availability on " + dayOfWeek);
        }
    }
}
