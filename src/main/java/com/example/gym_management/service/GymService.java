package com.example.gym_management.service;

import com.example.gym_management.dto.GymRequest;
import com.example.gym_management.dto.GymResponse;
import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import com.example.gym_management.mapper.GymMapper;
import com.example.gym_management.mapper.RoomMapper;
import com.example.gym_management.mapper.ScheduledClassMapper;
import com.example.gym_management.mapper.TrainerMapper;
import com.example.gym_management.repository.GymRepository;
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

@Service
@RequiredArgsConstructor
@Validated
public class GymService {

    private final GymRepository gymRepository;
    private final RoomRepository roomRepository;
    private final TrainerRepository trainerRepository;
    private final ScheduledClassRepository scheduledClassRepository;
    private final GymMapper gymMapper;
    private final RoomMapper roomMapper;
    private final TrainerMapper trainerMapper;
    private final ScheduledClassMapper scheduledClassMapper;

    @Transactional
    public GymResponse createGym(@Valid GymRequest request) {
        if (gymRepository.existsByName(request.getName())) {
            throw new IllegalStateException("A gym with name '" + request.getName() + "' already exists");
        }

        Gym gym = gymMapper.toEntity(request);
        Gym savedGym = gymRepository.save(gym);
        return gymMapper.toResponse(savedGym);
    }

    @Transactional(readOnly = true)
    public GymResponse getGymById(Long id) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + id));
        return gymMapper.toResponse(gym);
    }

    @Transactional(readOnly = true)
    public List<GymResponse> getAllGyms() {
        return gymMapper.toResponseList(gymRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<GymResponse> getActiveGyms() {
        return gymMapper.toResponseList(gymRepository.findByStatus(GymStatus.ACTIVE));
    }

    @Transactional(readOnly = true)
    public List<GymResponse> getGymsByStatus(GymStatus status) {
        return gymMapper.toResponseList(gymRepository.findByStatus(status));
    }

    @Transactional
    public GymResponse updateGym(Long id, @Valid GymRequest request) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + id));

        if (!gym.getName().equals(request.getName()) &&
                gymRepository.existsByName(request.getName())) {
            throw new IllegalStateException("A gym with name '" + request.getName() + "' already exists");
        }

        gymMapper.updateEntity(request, gym);
        Gym updatedGym = gymRepository.save(gym);
        return gymMapper.toResponse(updatedGym);
    }

    @Transactional
    public void deleteGym(Long id) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + id));

        if (!roomRepository.findByGymId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete gym with existing rooms. Please remove rooms first.");
        }

        if (!trainerRepository.findByGymId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete gym with existing trainers. Please remove trainers first.");
        }

        if (!scheduledClassRepository.findByGymId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete gym with existing scheduled classes. Please remove classes first.");
        }

        gymRepository.delete(gym);
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByGymId(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("Gym not found with id: " + gymId);
        }
        return roomMapper.toDtoList(roomRepository.findByGymId(gymId));
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> getTrainersByGymId(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("Gym not found with id: " + gymId);
        }
        return trainerMapper.toResponseListWithoutCount(trainerRepository.findByGymId(gymId));
    }

    @Transactional(readOnly = true)
    public List<ScheduledClassResponse> getScheduledClassesByGymId(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("Gym not found with id: " + gymId);
        }
        return scheduledClassMapper.toResponseList(scheduledClassRepository.findByGymId(gymId));
    }

    @Transactional(readOnly = true)
    public List<ScheduledClassResponse> getUpcomingClassesByGymId(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new IllegalArgumentException("Gym not found with id: " + gymId);
        }
        return scheduledClassMapper.toResponseList(
                scheduledClassRepository.findUpcomingClassesByGymId(gymId, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Gym getGymEntityById(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + id));
    }
}
