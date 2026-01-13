package com.example.gym_management.controller;

import com.example.gym_management.dto.ClassTypeRequest;
import com.example.gym_management.dto.ClassTypeResponse;
import com.example.gym_management.service.ClassTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-types")
@RequiredArgsConstructor
public class ClassTypeController {

  private final ClassTypeService classTypeService;

  @PostMapping
  public ResponseEntity<ClassTypeResponse> createClassType(@Valid @RequestBody ClassTypeRequest request) {
    ClassTypeResponse response = classTypeService.createClassType(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClassTypeResponse> getClassTypeById(@PathVariable Long id) {
    ClassTypeResponse response = classTypeService.getClassTypeById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ClassTypeResponse>> getAllClassTypes() {
    List<ClassTypeResponse> classTypes = classTypeService.getAllClassTypes();
    return ResponseEntity.ok(classTypes);
  }

  @GetMapping("/search")
  public ResponseEntity<List<ClassTypeResponse>> searchClassTypesByName(@RequestParam String name) {
    List<ClassTypeResponse> classTypes = classTypeService.searchClassTypesByName(name);
    return ResponseEntity.ok(classTypes);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClassTypeResponse> updateClassType(
      @PathVariable Long id,
      @Valid @RequestBody ClassTypeRequest request) {
    ClassTypeResponse response = classTypeService.updateClassType(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClassType(@PathVariable Long id) {
    classTypeService.deleteClassType(id);
    return ResponseEntity.noContent().build();
  }
}
