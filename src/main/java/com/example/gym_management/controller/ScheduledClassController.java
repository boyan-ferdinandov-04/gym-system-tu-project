package com.example.gym_management.controller;

import com.example.gym_management.dto.ScheduledClassRequest;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.service.ScheduledClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scheduled-classes")
@RequiredArgsConstructor
public class ScheduledClassController {

  private final ScheduledClassService scheduledClassService;

  @PostMapping
  public ResponseEntity<ScheduledClassResponse> createScheduledClass(
      @Valid @RequestBody ScheduledClassRequest request) {
    ScheduledClassResponse response = scheduledClassService.createScheduledClass(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ScheduledClassResponse> getScheduledClassById(@PathVariable Long id) {
    ScheduledClassResponse response = scheduledClassService.getScheduledClassById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ScheduledClassResponse>> getAllScheduledClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getAllScheduledClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/upcoming")
  public ResponseEntity<List<ScheduledClassResponse>> getUpcomingClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getUpcomingClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/available")
  public ResponseEntity<List<ScheduledClassResponse>> getAvailableClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getAvailableClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-trainer/{trainerId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByTrainer(@PathVariable Long trainerId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByTrainer(trainerId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-room/{roomId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByRoom(@PathVariable Long roomId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByRoom(roomId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-type/{classTypeId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByType(@PathVariable Long classTypeId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByType(classTypeId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-date-range")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByDateRange(startDate, endDate);
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ScheduledClassResponse> updateScheduledClass(
      @PathVariable Long id,
      @Valid @RequestBody ScheduledClassRequest request) {
    ScheduledClassResponse response = scheduledClassService.updateScheduledClass(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteScheduledClass(@PathVariable Long id) {
    scheduledClassService.deleteScheduledClass(id);
    return ResponseEntity.noContent().build();
  }
}
