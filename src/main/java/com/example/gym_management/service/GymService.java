package com.example.gym_management.service;

import com.example.gym_management.dto.GymRequest;
import com.example.gym_management.dto.GymResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.mapper.GymMapper;
import com.example.gym_management.repository.GymRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class GymService {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

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
        gymRepository.delete(gym);
    }
}
