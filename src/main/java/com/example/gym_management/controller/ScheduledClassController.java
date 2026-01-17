package com.example.gym_management.controller;

import com.example.gym_management.dto.ScheduledClassRequest;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.service.ScheduledClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scheduled-classes")
@RequiredArgsConstructor
@Tag(name = "Scheduled Classes", description = "Scheduled class management operations")
public class ScheduledClassController {

  private final ScheduledClassService scheduledClassService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @Operation(summary = "Create a scheduled class", description = "Schedules a new class with a specific trainer, room, and time. Requires ADMIN or MANAGER role.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Scheduled class created successfully", content = @Content(schema = @Schema(implementation = ScheduledClassResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request data or scheduling conflict", content = @Content),
      @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
      @ApiResponse(responseCode = "404", description = "Class type, trainer, or room not found", content = @Content)
  })
  public ResponseEntity<ScheduledClassResponse> createScheduledClass(
      @Valid @RequestBody ScheduledClassRequest request) {
    ScheduledClassResponse response = scheduledClassService.createScheduledClass(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get scheduled class by ID", description = "Retrieves a scheduled class by its unique identifier.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scheduled class found", content = @Content(schema = @Schema(implementation = ScheduledClassResponse.class))),
      @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
  })
  public ResponseEntity<ScheduledClassResponse> getScheduledClassById(
      @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long id) {
    ScheduledClassResponse response = scheduledClassService.getScheduledClassById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ScheduledClassResponse>> getAllScheduledClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getAllScheduledClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}")
  public ResponseEntity<List<ScheduledClassResponse>> getScheduledClassesByGymId(@PathVariable Long gymId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getScheduledClassesByGymId(gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/upcoming")
  public ResponseEntity<List<ScheduledClassResponse>> getUpcomingClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getUpcomingClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/upcoming")
  public ResponseEntity<List<ScheduledClassResponse>> getUpcomingClassesByGymId(@PathVariable Long gymId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getUpcomingClassesByGymId(gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/available")
  public ResponseEntity<List<ScheduledClassResponse>> getAvailableClasses() {
    List<ScheduledClassResponse> responses = scheduledClassService.getAvailableClasses();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/available")
  public ResponseEntity<List<ScheduledClassResponse>> getAvailableClassesByGymId(@PathVariable Long gymId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getAvailableClassesByGymId(gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-trainer/{trainerId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByTrainer(@PathVariable Long trainerId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByTrainer(trainerId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/by-trainer/{trainerId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByTrainerAndGymId(
      @PathVariable Long gymId,
      @PathVariable Long trainerId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByTrainerAndGymId(trainerId, gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-room/{roomId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByRoom(@PathVariable Long roomId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByRoom(roomId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/by-room/{roomId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByRoomAndGymId(
      @PathVariable Long gymId,
      @PathVariable Long roomId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByRoomAndGymId(roomId, gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-type/{classTypeId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByType(@PathVariable Long classTypeId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByType(classTypeId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/by-type/{classTypeId}")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByTypeAndGymId(
      @PathVariable Long gymId,
      @PathVariable Long classTypeId) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByTypeAndGymId(classTypeId, gymId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/by-date-range")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByDateRange(startDate, endDate);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/gym/{gymId}/by-date-range")
  public ResponseEntity<List<ScheduledClassResponse>> getClassesByDateRangeAndGymId(
      @PathVariable Long gymId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<ScheduledClassResponse> responses = scheduledClassService.getClassesByDateRangeAndGymId(gymId, startDate,
        endDate);
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ScheduledClassResponse> updateScheduledClass(
      @PathVariable Long id,
      @Valid @RequestBody ScheduledClassRequest request) {
    ScheduledClassResponse response = scheduledClassService.updateScheduledClass(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @Operation(summary = "Delete scheduled class", description = "Deletes a scheduled class. Requires ADMIN or MANAGER role.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Scheduled class deleted successfully"),
      @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
      @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
  })
  public ResponseEntity<Void> deleteScheduledClass(
      @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long id) {
    scheduledClassService.deleteScheduledClass(id);
    return ResponseEntity.noContent().build();
  }
}
