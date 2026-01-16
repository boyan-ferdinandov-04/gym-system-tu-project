package com.example.gym_management.controller;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.service.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Gym room management operations")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a room", description = "Creates a new gym room. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room created successfully",
                    content = @Content(schema = @Schema(implementation = RoomDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomDTO response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Retrieves a room by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room found",
                    content = @Content(schema = @Schema(implementation = RoomDTO.class))),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content)
    })
    public ResponseEntity<RoomDTO> getRoomById(
            @Parameter(description = "Room ID", required = true) @PathVariable Long id) {
        RoomDTO response = roomService.getRoomById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all rooms", description = "Retrieves all gym rooms.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoomDTO.class))))
    })
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByGymId(@PathVariable Long gymId) {
        List<RoomDTO> rooms = roomService.getRoomsByGymId(gymId);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update room", description = "Updates an existing room. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully",
                    content = @Content(schema = @Schema(implementation = RoomDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content)
    })
    public ResponseEntity<RoomDTO> updateRoom(
            @Parameter(description = "Room ID", required = true) @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        RoomDTO response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete room", description = "Deletes a room. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content)
    })
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "Room ID", required = true) @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
