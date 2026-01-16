package com.example.gym_management.mapper;

import com.example.gym_management.dto.GymDTO;
import com.example.gym_management.dto.GymRequest;
import com.example.gym_management.dto.GymResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GymMapper {

    public GymResponse toResponse(Gym gym) {
        if (gym == null) {
            return null;
        }
        return new GymResponse(
                gym.getId(),
                gym.getName(),
                gym.getAddress(),
                gym.getPhoneNumber(),
                gym.getEmail(),
                gym.getStatus(),
                gym.getOpeningTime(),
                gym.getClosingTime()
        );
    }

    public GymDTO toDto(Gym gym) {
        if (gym == null) {
            return null;
        }
        return new GymDTO(
                gym.getId(),
                gym.getName()
        );
    }

    public List<GymResponse> toResponseList(List<Gym> gyms) {
        return gyms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<GymDTO> toDtoList(List<Gym> gyms) {
        return gyms.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Gym toEntity(GymRequest request) {
        Gym gym = new Gym();
        gym.setName(request.getName());
        gym.setAddress(request.getAddress());
        gym.setPhoneNumber(request.getPhoneNumber());
        gym.setEmail(request.getEmail());
        gym.setStatus(request.getStatus() != null ? request.getStatus() : GymStatus.ACTIVE);
        gym.setOpeningTime(request.getOpeningTime());
        gym.setClosingTime(request.getClosingTime());
        return gym;
    }

    public void updateEntity(GymRequest request, Gym gym) {
        gym.setName(request.getName());
        gym.setAddress(request.getAddress());
        gym.setPhoneNumber(request.getPhoneNumber());
        gym.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            gym.setStatus(request.getStatus());
        }
        gym.setOpeningTime(request.getOpeningTime());
        gym.setClosingTime(request.getClosingTime());
    }
}
