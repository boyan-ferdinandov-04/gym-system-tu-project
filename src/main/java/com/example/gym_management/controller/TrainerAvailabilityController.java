package com.example.gym_management.controller;

import com.example.gym_management.dto.*;
import com.example.gym_management.service.TrainerAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer Availability", description = "Trainer availability and time-off management operations")
public class TrainerAvailabilityController {

    private final TrainerAvailabilityService trainerAvailabilityService;

    @GetMapping("/{id}/availability")
    @Operation(summary = "Get trainer availability", description = "Retrieves all availability slots for a specific trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability slots retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerAvailabilityResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<List<TrainerAvailabilityResponse>> getTrainerAvailability(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id) {
        List<TrainerAvailabilityResponse> availability = trainerAvailabilityService.getTrainerAvailability(id);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create availability slot", description = "Creates a new availability slot for a trainer. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Availability slot created successfully",
                    content = @Content(schema = @Schema(implementation = TrainerAvailabilityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<TrainerAvailabilityResponse> createAvailability(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.createAvailability(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/availability/{slotId}")
    @Operation(summary = "Get availability slot", description = "Retrieves a specific availability slot for a trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability slot found",
                    content = @Content(schema = @Schema(implementation = TrainerAvailabilityResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer or slot not found", content = @Content)
    })
    public ResponseEntity<TrainerAvailabilityResponse> getAvailabilitySlot(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Availability Slot ID", required = true) @PathVariable Long slotId) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.getAvailabilityById(id, slotId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/availability/{slotId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update availability slot", description = "Updates an existing availability slot. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability slot updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerAvailabilityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or slot not found", content = @Content)
    })
    public ResponseEntity<TrainerAvailabilityResponse> updateAvailability(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Availability Slot ID", required = true) @PathVariable Long slotId,
            @Valid @RequestBody TrainerAvailabilityRequest request) {
        TrainerAvailabilityResponse response = trainerAvailabilityService.updateAvailability(id, slotId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/availability/{slotId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete availability slot", description = "Deletes an availability slot. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Availability slot deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or slot not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAvailability(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Availability Slot ID", required = true) @PathVariable Long slotId) {
        trainerAvailabilityService.deleteAvailability(id, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/time-off")
    @Operation(summary = "Get trainer time-off", description = "Retrieves all time-off records for a specific trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time-off records retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerTimeOffResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<List<TrainerTimeOffResponse>> getTrainerTimeOff(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id) {
        List<TrainerTimeOffResponse> timeOffs = trainerAvailabilityService.getTrainerTimeOff(id);
        return ResponseEntity.ok(timeOffs);
    }

    @GetMapping("/{id}/time-off/upcoming")
    @Operation(summary = "Get upcoming time-off", description = "Retrieves upcoming time-off records for a trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming time-off records retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerTimeOffResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<List<TrainerTimeOffResponse>> getUpcomingTimeOff(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id) {
        List<TrainerTimeOffResponse> timeOffs = trainerAvailabilityService.getUpcomingTimeOff(id);
        return ResponseEntity.ok(timeOffs);
    }

    @PostMapping("/{id}/time-off")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create time-off", description = "Creates a new time-off record for a trainer. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Time-off created successfully",
                    content = @Content(schema = @Schema(implementation = TrainerTimeOffResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<TrainerTimeOffResponse> createTimeOff(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TrainerTimeOffRequest request) {
        TrainerTimeOffResponse response = trainerAvailabilityService.createTimeOff(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/time-off/{timeOffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete time-off", description = "Deletes a time-off record. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Time-off deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or time-off not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTimeOff(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Time-off ID", required = true) @PathVariable Long timeOffId) {
        trainerAvailabilityService.deleteTimeOff(id, timeOffId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    @Operation(summary = "Find available trainers", description = "Finds trainers available at a specific date and time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available trainers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AvailableTrainerDTO.class))))
    })
    public ResponseEntity<List<AvailableTrainerDTO>> findAvailableTrainers(
            @Parameter(description = "Date to check availability (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Time to check availability (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        List<AvailableTrainerDTO> availableTrainers = trainerAvailabilityService.findAvailableTrainers(date, time);
        return ResponseEntity.ok(availableTrainers);
    }
}
