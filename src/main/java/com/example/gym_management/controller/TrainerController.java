package com.example.gym_management.controller;

import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
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

    @GetMapping("/specialization")
    public ResponseEntity<List<TrainerResponse>> getTrainersBySpecialization(@RequestParam String spec) {
        List<TrainerResponse> trainers = trainerService.getTrainersBySpecialization(spec);
        return ResponseEntity.ok(trainers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponse> updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.updateTrainer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}
