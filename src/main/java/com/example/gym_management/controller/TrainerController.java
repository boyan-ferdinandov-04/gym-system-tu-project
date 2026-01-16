package com.example.gym_management.controller;

import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.service.TrainerService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers", description = "Trainer management operations")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new trainer", description = "Creates a new trainer. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.createTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trainer by ID", description = "Retrieves a trainer by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer found",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<TrainerResponse> getTrainerById(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id) {
        TrainerResponse response = trainerService.getTrainerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all trainers", description = "Retrieves all trainers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerResponse.class))))
    })
    public ResponseEntity<List<TrainerResponse>> getAllTrainers() {
        List<TrainerResponse> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search trainers by name", description = "Searches for trainers by their first or last name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerResponse.class))))
    })
    public ResponseEntity<List<TrainerResponse>> searchTrainersByName(
            @Parameter(description = "Name to search for", required = true) @RequestParam String name) {
        List<TrainerResponse> trainers = trainerService.searchTrainersByName(name);
        return ResponseEntity.ok(trainers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update trainer", description = "Updates an existing trainer's information. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<TrainerResponse> updateTrainer(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Valid @RequestBody TrainerRequest request) {
        TrainerResponse response = trainerService.updateTrainer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete trainer", description = "Deletes a trainer. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTrainer(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/class-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Assign class types to trainer", description = "Assigns class types that the trainer can teach. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class types assigned successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or class type not found", content = @Content)
    })
    public ResponseEntity<TrainerResponse> assignClassTypes(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Set of class type IDs to assign") @RequestBody Set<Long> classTypeIds) {
        TrainerResponse response = trainerService.assignClassTypes(id, classTypeIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/class-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Remove class types from trainer", description = "Removes class types from a trainer. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class types removed successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<TrainerResponse> removeClassTypes(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Set of class type IDs to remove") @RequestBody Set<Long> classTypeIds) {
        TrainerResponse response = trainerService.removeClassTypes(id, classTypeIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-class-type/{classTypeId}")
    @Operation(summary = "Get trainers by class type", description = "Retrieves all trainers qualified to teach a specific class type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Class type not found", content = @Content)
    })
    public ResponseEntity<List<TrainerResponse>> getTrainersByClassType(
            @Parameter(description = "Class Type ID", required = true) @PathVariable Long classTypeId) {
        List<TrainerResponse> trainers = trainerService.getTrainersByClassType(classTypeId);
        return ResponseEntity.ok(trainers);
    }
}
