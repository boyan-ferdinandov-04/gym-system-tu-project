package com.example.gym_management.controller;

import com.example.gym_management.dto.*;
import com.example.gym_management.service.TrainerAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerAvailabilityController {

    private final TrainerAvailabilityService trainerAvailabilityService;

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<TrainerAvailabilityResponse>> getTrainerAvailability(@PathVariable Long id) {
        List<TrainerAvailabilityResponse> availability = trainerAvailabilityService.getTrainerAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerAvailabilityResponse> createAvailability(
            @PathVariable Long id,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.createAvailability(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/availability/{slotId}")
    public ResponseEntity<TrainerAvailabilityResponse> getAvailabilitySlot(
            @PathVariable Long id,
            @PathVariable Long slotId) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.getAvailabilityById(id, slotId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/availability/{slotId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerAvailabilityResponse> updateAvailability(
            @PathVariable Long id,
            @PathVariable Long slotId,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.updateAvailability(id, slotId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/availability/{slotId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            @PathVariable Long slotId) {
        trainerAvailabilityService.deleteAvailability(id, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/time-off")
    public ResponseEntity<List<TrainerTimeOffResponse>> getTrainerTimeOff(@PathVariable Long id) {
        List<TrainerTimeOffResponse> timeOffs = trainerAvailabilityService.getTrainerTimeOff(id);
        return ResponseEntity.ok(timeOffs);
    }

    @GetMapping("/{id}/time-off/upcoming")
    public ResponseEntity<List<TrainerTimeOffResponse>> getUpcomingTimeOff(@PathVariable Long id) {
        List<TrainerTimeOffResponse> timeOffs = trainerAvailabilityService.getUpcomingTimeOff(id);
        return ResponseEntity.ok(timeOffs);
    }

    @PostMapping("/{id}/time-off")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerTimeOffResponse> createTimeOff(
            @PathVariable Long id,
            @Valid @RequestBody TrainerTimeOffRequest request) {
        TrainerTimeOffResponse response = trainerAvailabilityService.createTimeOff(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/time-off/{timeOffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTimeOff(
            @PathVariable Long id,
            @PathVariable Long timeOffId) {
        trainerAvailabilityService.deleteTimeOff(id, timeOffId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableTrainerDTO>> findAvailableTrainers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        List<AvailableTrainerDTO> availableTrainers = trainerAvailabilityService.findAvailableTrainers(date, time);
        return ResponseEntity.ok(availableTrainers);
    }
}
