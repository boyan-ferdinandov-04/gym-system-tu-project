package com.example.gym_management.controller;

import com.example.gym_management.dto.ClassTypeRequest;
import com.example.gym_management.dto.ClassTypeResponse;
import com.example.gym_management.service.ClassTypeService;
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
@RequestMapping("/api/class-types")
@RequiredArgsConstructor
@Tag(name = "Class Types", description = "Class type management operations (e.g., Yoga, Pilates, CrossFit)")
public class ClassTypeController {

    private final ClassTypeService classTypeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a class type", description = "Creates a new class type. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Class type created successfully",
                    content = @Content(schema = @Schema(implementation = ClassTypeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<ClassTypeResponse> createClassType(@Valid @RequestBody ClassTypeRequest request) {
        ClassTypeResponse response = classTypeService.createClassType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get class type by ID", description = "Retrieves a class type by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class type found",
                    content = @Content(schema = @Schema(implementation = ClassTypeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Class type not found", content = @Content)
    })
    public ResponseEntity<ClassTypeResponse> getClassTypeById(
            @Parameter(description = "Class Type ID", required = true) @PathVariable Long id) {
        ClassTypeResponse response = classTypeService.getClassTypeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all class types", description = "Retrieves all class types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class types retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClassTypeResponse.class))))
    })
    public ResponseEntity<List<ClassTypeResponse>> getAllClassTypes() {
        List<ClassTypeResponse> classTypes = classTypeService.getAllClassTypes();
        return ResponseEntity.ok(classTypes);
    }

    @GetMapping("/search")
    @Operation(summary = "Search class types by name", description = "Searches for class types by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClassTypeResponse.class))))
    })
    public ResponseEntity<List<ClassTypeResponse>> searchClassTypesByName(
            @Parameter(description = "Name to search for", required = true) @RequestParam String name) {
        List<ClassTypeResponse> classTypes = classTypeService.searchClassTypesByName(name);
        return ResponseEntity.ok(classTypes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update class type", description = "Updates an existing class type. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class type updated successfully",
                    content = @Content(schema = @Schema(implementation = ClassTypeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class type not found", content = @Content)
    })
    public ResponseEntity<ClassTypeResponse> updateClassType(
            @Parameter(description = "Class Type ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ClassTypeRequest request) {
        ClassTypeResponse response = classTypeService.updateClassType(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete class type", description = "Deletes a class type. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Class type deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Class type not found", content = @Content)
    })
    public ResponseEntity<Void> deleteClassType(
            @Parameter(description = "Class Type ID", required = true) @PathVariable Long id) {
        classTypeService.deleteClassType(id);
        return ResponseEntity.noContent().build();
    }
}
