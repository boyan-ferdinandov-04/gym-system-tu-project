package com.example.gym_management.controller;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomDTO response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        RoomDTO response = roomService.getRoomById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        RoomDTO response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
