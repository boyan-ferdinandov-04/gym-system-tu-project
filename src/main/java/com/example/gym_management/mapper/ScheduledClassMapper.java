package com.example.gym_management.mapper;

import com.example.gym_management.dto.GymDTO;
import com.example.gym_management.dto.ScheduledClassRequest;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.Room;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.repository.ClassTypeRepository;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.RoomRepository;
import com.example.gym_management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduledClassMapper {

    private final GymRepository gymRepository;
    private final ClassTypeRepository classTypeRepository;
    private final TrainerRepository trainerRepository;
    private final RoomRepository roomRepository;
    private final ClassTypeMapper classTypeMapper;
    private final TrainerMapper trainerMapper;
    private final RoomMapper roomMapper;

    public ScheduledClassResponse toResponse(ScheduledClass scheduledClass) {
        if (scheduledClass == null) {
            return null;
        }

        Integer bookingCount = calculateBookingCount(scheduledClass);
        Integer availableSpots = calculateAvailableSpots(scheduledClass, bookingCount);

        return new ScheduledClassResponse(
                scheduledClass.getId(),
                toGymDto(scheduledClass.getGym()),
                classTypeMapper.toSimpleDto(scheduledClass.getClassType()),
                trainerMapper.toSimpleDto(scheduledClass.getTrainer()),
                roomMapper.toDto(scheduledClass.getRoom()),
                scheduledClass.getStartTime(),
                bookingCount,
                availableSpots
        );
    }

    public List<ScheduledClassResponse> toResponseList(List<ScheduledClass> scheduledClasses) {
        return scheduledClasses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ScheduledClass toEntity(ScheduledClassRequest request) {
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Gym not found with id: " + request.getGymId()));

        ClassType classType = classTypeRepository.findById(request.getClassTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Class type not found with id: " + request.getClassTypeId()));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainer not found with id: " + request.getTrainerId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found with id: " + request.getRoomId()));

        ScheduledClass scheduledClass = new ScheduledClass();
        scheduledClass.setGym(gym);
        scheduledClass.setClassType(classType);
        scheduledClass.setTrainer(trainer);
        scheduledClass.setRoom(room);
        scheduledClass.setStartTime(request.getStartTime());
        return scheduledClass;
    }

    public ScheduledClass toEntityWithGym(ScheduledClassRequest request, Gym gym) {
        ClassType classType = classTypeRepository.findById(request.getClassTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Class type not found with id: " + request.getClassTypeId()));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainer not found with id: " + request.getTrainerId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found with id: " + request.getRoomId()));

        ScheduledClass scheduledClass = new ScheduledClass();
        scheduledClass.setGym(gym);
        scheduledClass.setClassType(classType);
        scheduledClass.setTrainer(trainer);
        scheduledClass.setRoom(room);
        scheduledClass.setStartTime(request.getStartTime());
        return scheduledClass;
    }

    public void updateEntity(ScheduledClassRequest request, ScheduledClass scheduledClass) {
        if (request.getGymId() != null && !request.getGymId().equals(scheduledClass.getGym().getId())) {
            Gym gym = gymRepository.findById(request.getGymId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Gym not found with id: " + request.getGymId()));
            scheduledClass.setGym(gym);
        }

        ClassType classType = classTypeRepository.findById(request.getClassTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Class type not found with id: " + request.getClassTypeId()));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainer not found with id: " + request.getTrainerId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room not found with id: " + request.getRoomId()));

        scheduledClass.setClassType(classType);
        scheduledClass.setTrainer(trainer);
        scheduledClass.setRoom(room);
        scheduledClass.setStartTime(request.getStartTime());
    }

    private GymDTO toGymDto(Gym gym) {
        if (gym == null) {
            return null;
        }
        return new GymDTO(gym.getId(), gym.getName());
    }

    private Integer calculateBookingCount(ScheduledClass scheduledClass) {
        return scheduledClass.getBookings() != null
                ? scheduledClass.getBookings().size()
                : 0;
    }

    private Integer calculateAvailableSpots(ScheduledClass scheduledClass, Integer bookingCount) {
        Integer capacity = scheduledClass.getRoom() != null
                ? scheduledClass.getRoom().getCapacity()
                : 0;
        return capacity - bookingCount;
    }
}
