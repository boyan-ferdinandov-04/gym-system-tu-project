package com.example.gym_management.mapper;

import com.example.gym_management.dto.GymRequest;
import com.example.gym_management.dto.GymResponse;
import com.example.gym_management.entity.Gym;
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
                gym.getPhoneNumber()
        );
    }

    public List<GymResponse> toResponseList(List<Gym> gyms) {
        return gyms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Gym toEntity(GymRequest request) {
        return new Gym(
                request.getName(),
                request.getAddress(),
                request.getPhoneNumber()
        );
    }

    public void updateEntity(GymRequest request, Gym gym) {
        gym.setName(request.getName());
        gym.setAddress(request.getAddress());
        gym.setPhoneNumber(request.getPhoneNumber());
    }
}
