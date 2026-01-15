package com.example.gym_management.service;

import com.example.gym_management.dto.ClassTypeRequest;
import com.example.gym_management.dto.ClassTypeResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.mapper.ClassTypeMapper;
import com.example.gym_management.repository.ClassTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassTypeServiceTest {

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private ClassTypeMapper classTypeMapper;

    @InjectMocks
    private ClassTypeService classTypeService;

    private ClassType classType;
    private ClassTypeRequest classTypeRequest;
    private ClassTypeResponse classTypeResponse;

    @BeforeEach
    void setUp() {
        classType = new ClassType("Yoga", "Relaxing yoga class");
        classType.setId(1L);
        classType.setScheduledClasses(new ArrayList<>());

        classTypeRequest = new ClassTypeRequest("Yoga", "Relaxing yoga class");

        classTypeResponse = new ClassTypeResponse(1L, "Yoga", "Relaxing yoga class", 0);
    }

    @Test
    void createClassType_Success() {
        when(classTypeRepository.existsByName("Yoga")).thenReturn(false);
        when(classTypeMapper.toEntity(classTypeRequest)).thenReturn(classType);
        when(classTypeRepository.save(classType)).thenReturn(classType);
        when(classTypeMapper.toResponseWithoutCount(classType)).thenReturn(classTypeResponse);

        ClassTypeResponse result = classTypeService.createClassType(classTypeRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga");
        verify(classTypeRepository).save(classType);
    }

    @Test
    void createClassType_DuplicateName_ThrowsException() {
        when(classTypeRepository.existsByName("Yoga")).thenReturn(true);

        assertThatThrownBy(() -> classTypeService.createClassType(classTypeRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        verify(classTypeRepository, never()).save(any());
    }

    @Test
    void getClassTypeById_Success() {
        when(classTypeRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(classType));
        when(classTypeMapper.toResponse(classType)).thenReturn(classTypeResponse);

        ClassTypeResponse result = classTypeService.getClassTypeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getClassTypeById_NotFound_ThrowsException() {
        when(classTypeRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classTypeService.getClassTypeById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class type not found");
    }

    @Test
    void getAllClassTypes_Success() {
        List<ClassType> classTypes = List.of(classType);
        List<ClassTypeResponse> responses = List.of(classTypeResponse);

        when(classTypeRepository.findAll()).thenReturn(classTypes);
        when(classTypeMapper.toResponseListWithoutCount(classTypes)).thenReturn(responses);

        List<ClassTypeResponse> result = classTypeService.getAllClassTypes();

        assertThat(result).hasSize(1);
    }

    @Test
    void searchClassTypesByName_Success() {
        List<ClassType> classTypes = List.of(classType);
        List<ClassTypeResponse> responses = List.of(classTypeResponse);

        when(classTypeRepository.searchByName("Yoga")).thenReturn(classTypes);
        when(classTypeMapper.toResponseListWithoutCount(classTypes)).thenReturn(responses);

        List<ClassTypeResponse> result = classTypeService.searchClassTypesByName("Yoga");

        assertThat(result).hasSize(1);
    }

    @Test
    void updateClassType_Success() {
        ClassTypeRequest updateRequest = new ClassTypeRequest("Pilates", "Core strengthening class");
        ClassTypeResponse updatedResponse = new ClassTypeResponse(1L, "Pilates", "Core strengthening class", null);

        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(classType));
        when(classTypeRepository.existsByName("Pilates")).thenReturn(false);
        when(classTypeRepository.save(classType)).thenReturn(classType);
        when(classTypeMapper.toResponseWithoutCount(classType)).thenReturn(updatedResponse);

        ClassTypeResponse result = classTypeService.updateClassType(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pilates");
        verify(classTypeMapper).updateEntity(updateRequest, classType);
        verify(classTypeRepository).save(classType);
    }

    @Test
    void updateClassType_NotFound_ThrowsException() {
        when(classTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classTypeService.updateClassType(1L, classTypeRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class type not found");
    }

    @Test
    void updateClassType_DuplicateName_ThrowsException() {
        ClassTypeRequest updateRequest = new ClassTypeRequest("Pilates", "Core class");

        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(classType));
        when(classTypeRepository.existsByName("Pilates")).thenReturn(true);

        assertThatThrownBy(() -> classTypeService.updateClassType(1L, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void deleteClassType_Success() {
        when(classTypeRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(classType));

        classTypeService.deleteClassType(1L);

        verify(classTypeRepository).delete(classType);
    }

    @Test
    void deleteClassType_NotFound_ThrowsException() {
        when(classTypeRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classTypeService.deleteClassType(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class type not found");
    }

    @Test
    void deleteClassType_WithScheduledClasses_ThrowsException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        classType.setScheduledClasses(List.of(scheduledClass));

        when(classTypeRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(classType));

        assertThatThrownBy(() -> classTypeService.deleteClassType(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete class type with scheduled classes");

        verify(classTypeRepository, never()).delete(any());
    }
}
