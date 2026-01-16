package com.example.gym_management.controller;

import com.example.gym_management.dto.GymRequest;
import com.example.gym_management.dto.GymResponse;
import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.GymStatus;
import com.example.gym_management.service.GymService;
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

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
@Tag(name = "Gyms", description = "Gym location management operations")
public class GymController {

    private final GymService gymService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new gym", description = "Creates a new gym location. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Gym created successfully",
                    content = @Content(schema = @Schema(implementation = GymResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
    })
    public ResponseEntity<GymResponse> createGym(@Valid @RequestBody GymRequest request) {
        GymResponse response = gymService.createGym(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserInGym(#id)")
    @Operation(summary = "Get gym by ID", description = "Retrieves a gym by its unique identifier. Requires ADMIN role or membership in the gym.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym found",
                    content = @Content(schema = @Schema(implementation = GymResponse.class))),
            @ApiResponse(responseCode = "404", description = "Gym not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<GymResponse> getGymById(
            @Parameter(description = "Gym ID", required = true) @PathVariable Long id) {
        GymResponse response = gymService.getGymById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all gyms", description = "Retrieves all gym locations. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gyms retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = GymResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<List<GymResponse>> getAllGyms() {
        List<GymResponse> response = gymService.getAllGyms();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<GymResponse>> getActiveGyms() {
        List<GymResponse> response = gymService.getActiveGyms();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GymResponse>> getGymsByStatus(@PathVariable GymStatus status) {
        List<GymResponse> response = gymService.getGymsByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update gym", description = "Updates an existing gym's information. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gym updated successfully",
                    content = @Content(schema = @Schema(implementation = GymResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gym not found", content = @Content)
    })
    public ResponseEntity<GymResponse> updateGym(
            @Parameter(description = "Gym ID", required = true) @PathVariable Long id,
            @Valid @RequestBody GymRequest request) {
        GymResponse response = gymService.updateGym(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete gym", description = "Deletes a gym location. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Gym deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gym not found", content = @Content)
    })
    public ResponseEntity<Void> deleteGym(
            @Parameter(description = "Gym ID", required = true) @PathVariable Long id) {
        gymService.deleteGym(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/rooms")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserInGym(#id)")
    public ResponseEntity<List<RoomDTO>> getRoomsByGymId(@PathVariable Long id) {
        List<RoomDTO> response = gymService.getRoomsByGymId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/trainers")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserInGym(#id)")
    public ResponseEntity<List<TrainerResponse>> getTrainersByGymId(@PathVariable Long id) {
        List<TrainerResponse> response = gymService.getTrainersByGymId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/classes")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserInGym(#id)")
    public ResponseEntity<List<ScheduledClassResponse>> getClassesByGymId(@PathVariable Long id) {
        List<ScheduledClassResponse> response = gymService.getScheduledClassesByGymId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/classes/upcoming")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserInGym(#id)")
    public ResponseEntity<List<ScheduledClassResponse>> getUpcomingClassesByGymId(@PathVariable Long id) {
        List<ScheduledClassResponse> response = gymService.getUpcomingClassesByGymId(id);
        return ResponseEntity.ok(response);
    }
}
