package com.example.gym_management.controller;

import com.example.gym_management.dto.ScheduledClassRequest;
import com.example.gym_management.dto.ScheduledClassResponse;
import com.example.gym_management.service.ScheduledClassService;
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
            @ApiResponse(responseCode = "201", description = "Scheduled class created successfully",
                    content = @Content(schema = @Schema(implementation = ScheduledClassResponse.class))),
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
            @ApiResponse(responseCode = "200", description = "Scheduled class found",
                    content = @Content(schema = @Schema(implementation = ScheduledClassResponse.class))),
            @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
    })
    public ResponseEntity<ScheduledClassResponse> getScheduledClassById(
            @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long id) {
        ScheduledClassResponse response = scheduledClassService.getScheduledClassById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all scheduled classes", description = "Retrieves all scheduled classes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class))))
    })
    public ResponseEntity<List<ScheduledClassResponse>> getAllScheduledClasses() {
        List<ScheduledClassResponse> responses = scheduledClassService.getAllScheduledClasses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming classes", description = "Retrieves all upcoming scheduled classes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class))))
    })
    public ResponseEntity<List<ScheduledClassResponse>> getUpcomingClasses() {
        List<ScheduledClassResponse> responses = scheduledClassService.getUpcomingClasses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available classes", description = "Retrieves upcoming classes that have available spots for booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class))))
    })
    public ResponseEntity<List<ScheduledClassResponse>> getAvailableClasses() {
        List<ScheduledClassResponse> responses = scheduledClassService.getAvailableClasses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-trainer/{trainerId}")
    @Operation(summary = "Get classes by trainer", description = "Retrieves all scheduled classes for a specific trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    public ResponseEntity<List<ScheduledClassResponse>> getClassesByTrainer(
            @Parameter(description = "Trainer ID", required = true) @PathVariable Long trainerId) {
        List<ScheduledClassResponse> responses = scheduledClassService.getClassesByTrainer(trainerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-room/{roomId}")
    @Operation(summary = "Get classes by room", description = "Retrieves all scheduled classes for a specific room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content)
    })
    public ResponseEntity<List<ScheduledClassResponse>> getClassesByRoom(
            @Parameter(description = "Room ID", required = true) @PathVariable Long roomId) {
        List<ScheduledClassResponse> responses = scheduledClassService.getClassesByRoom(roomId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-type/{classTypeId}")
    @Operation(summary = "Get classes by type", description = "Retrieves all scheduled classes of a specific type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Class type not found", content = @Content)
    })
    public ResponseEntity<List<ScheduledClassResponse>> getClassesByType(
            @Parameter(description = "Class Type ID", required = true) @PathVariable Long classTypeId) {
        List<ScheduledClassResponse> responses = scheduledClassService.getClassesByType(classTypeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Get classes by date range", description = "Retrieves all scheduled classes within a specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduledClassResponse.class))))
    })
    public ResponseEntity<List<ScheduledClassResponse>> getClassesByDateRange(
            @Parameter(description = "Start date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date-time (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ScheduledClassResponse> responses = scheduledClassService.getClassesByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update scheduled class", description = "Updates an existing scheduled class. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled class updated successfully",
                    content = @Content(schema = @Schema(implementation = ScheduledClassResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or scheduling conflict", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Scheduled class not found", content = @Content)
    })
    public ResponseEntity<ScheduledClassResponse> updateScheduledClass(
            @Parameter(description = "Scheduled Class ID", required = true) @PathVariable Long id,
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
