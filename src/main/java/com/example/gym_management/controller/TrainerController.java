package com.example.gym_management.controller;

import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.createTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getTrainerById(@PathVariable Long id) {
        TrainerResponse response = trainerService.getTrainerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllTrainers() {
        List<TrainerResponse> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainerResponse>> searchTrainersByName(@RequestParam String name) {
        List<TrainerResponse> trainers = trainerService.searchTrainersByName(name);
        return ResponseEntity.ok(trainers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerResponse> updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.updateTrainer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/class-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerResponse> assignClassTypes(
            @PathVariable Long id,
            @RequestBody Set<Long> classTypeIds) {
        TrainerResponse response = trainerService.assignClassTypes(id, classTypeIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/class-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TrainerResponse> removeClassTypes(
            @PathVariable Long id,
            @RequestBody Set<Long> classTypeIds) {
        TrainerResponse response = trainerService.removeClassTypes(id, classTypeIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-class-type/{classTypeId}")
    public ResponseEntity<List<TrainerResponse>> getTrainersByClassType(@PathVariable Long classTypeId) {
        List<TrainerResponse> trainers = trainerService.getTrainersByClassType(classTypeId);
        return ResponseEntity.ok(trainers);
    }
}
