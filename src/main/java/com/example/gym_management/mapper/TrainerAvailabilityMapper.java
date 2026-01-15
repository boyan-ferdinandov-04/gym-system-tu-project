package com.example.gym_management.mapper;

import com.example.gym_management.dto.*;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.entity.TrainerAvailability;
import com.example.gym_management.entity.TrainerTimeOff;
import com.example.gym_management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainerAvailabilityMapper {

    private final TrainerRepository trainerRepository;
    private final ClassTypeMapper classTypeMapper;

    public TrainerAvailabilityResponse toAvailabilityResponse(TrainerAvailability availability) {
        if (availability == null) {
            return null;
        }
        return new TrainerAvailabilityResponse(
                availability.getId(),
                availability.getTrainer().getId(),
                getTrainerFullName(availability.getTrainer()),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    public List<TrainerAvailabilityResponse> toAvailabilityResponseList(List<TrainerAvailability> availabilities) {
        return availabilities.stream()
                .map(this::toAvailabilityResponse)
                .collect(Collectors.toList());
    }

    public TrainerAvailability toAvailabilityEntity(TrainerAvailabilityRequest request, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainerId));

        return new TrainerAvailability(
                trainer,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    public void updateAvailabilityEntity(TrainerAvailabilityRequest request, TrainerAvailability availability) {
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
    }

    public TrainerTimeOffResponse toTimeOffResponse(TrainerTimeOff timeOff) {
        if (timeOff == null) {
            return null;
        }
        return new TrainerTimeOffResponse(
                timeOff.getId(),
                timeOff.getTrainer().getId(),
                getTrainerFullName(timeOff.getTrainer()),
                timeOff.getDate(),
                timeOff.getReason()
        );
    }

    public List<TrainerTimeOffResponse> toTimeOffResponseList(List<TrainerTimeOff> timeOffs) {
        return timeOffs.stream()
                .map(this::toTimeOffResponse)
                .collect(Collectors.toList());
    }

    public TrainerTimeOff toTimeOffEntity(TrainerTimeOffRequest request, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainerId));

        return new TrainerTimeOff(
                trainer,
                request.getDate(),
                request.getReason()
        );
    }

    public AvailableTrainerDTO toAvailableTrainerDTO(Trainer trainer, TrainerAvailability availability) {
        if (trainer == null) {
            return null;
        }

        List<ClassTypeDTO> classTypeDTOs = trainer.getClassTypes() != null
                ? trainer.getClassTypes().stream()
                .map(classTypeMapper::toSimpleDto)
                .collect(Collectors.toList())
                : null;

        return new AvailableTrainerDTO(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                availability != null ? availability.getStartTime() : null,
                availability != null ? availability.getEndTime() : null,
                classTypeDTOs
        );
    }

    private String getTrainerFullName(Trainer trainer) {
        return trainer.getFirstName() + " " + trainer.getLastName();
    }
}
