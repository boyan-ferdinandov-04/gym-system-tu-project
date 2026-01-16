package com.example.gym_management.service;

import com.example.gym_management.dto.GymDTO;
import com.example.gym_management.dto.TrainerRequest;
import com.example.gym_management.dto.TrainerResponse;
import com.example.gym_management.entity.ClassType;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.mapper.TrainerMapper;
import com.example.gym_management.repository.ClassTypeRepository;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerService trainerService;

    private Gym gym;
    private GymDTO gymDTO;
    private Trainer trainer;
    private TrainerRequest trainerRequest;
    private TrainerResponse trainerResponse;
    private ClassType classType;

    @BeforeEach
    void setUp() {
        gym = new Gym("Main Gym", "123 Main St", "555-1234");
        gym.setId(1L);
        gym.setStatus(GymStatus.ACTIVE);

        gymDTO = new GymDTO(1L, "Main Gym");

        trainer = new Trainer(gym, "John", "Smith");
        trainer.setId(1L);
        trainer.setClassTypes(new HashSet<>());
        trainer.setScheduledClasses(new ArrayList<>());

        trainerRequest = new TrainerRequest(1L, "John", "Smith", null);

        trainerResponse = new TrainerResponse(1L, gymDTO, "John", "Smith", 0, new ArrayList<>());

        classType = new ClassType("Yoga", "Relaxing yoga class");
        classType.setId(1L);
    }

    @Test
    void createTrainer_Success() {
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));
        when(trainerMapper.toEntityWithGym(trainerRequest, gym)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponseWithClassTypes(trainer)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.createTrainer(trainerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void getTrainerById_Success() {
        when(trainerRepository.findByIdWithClassTypesAndScheduledClasses(1L)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponseWithClassTypes(trainer)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.getTrainerById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTrainerById_NotFound_ThrowsException() {
        when(trainerRepository.findByIdWithClassTypesAndScheduledClasses(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }

    @Test
    void getAllTrainers_Success() {
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponse> responses = List.of(trainerResponse);

        when(trainerRepository.findAll()).thenReturn(trainers);
        when(trainerMapper.toResponseListWithoutCount(trainers)).thenReturn(responses);

        List<TrainerResponse> result = trainerService.getAllTrainers();

        assertThat(result).hasSize(1);
    }

    @Test
    void searchTrainersByName_Success() {
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponse> responses = List.of(trainerResponse);

        when(trainerRepository.searchByName("John")).thenReturn(trainers);
        when(trainerMapper.toResponseListWithoutCount(trainers)).thenReturn(responses);

        List<TrainerResponse> result = trainerService.searchTrainersByName("John");

        assertThat(result).hasSize(1);
    }

    @Test
    void updateTrainer_Success() {
        TrainerRequest updateRequest = new TrainerRequest(1L, "Jane", "Smith", null);
        TrainerResponse updatedResponse = new TrainerResponse(1L, gymDTO, "Jane", "Smith", 0, new ArrayList<>());

        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponseWithClassTypes(trainer)).thenReturn(updatedResponse);

        TrainerResponse result = trainerService.updateTrainer(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        verify(trainerMapper).updateEntity(updateRequest, trainer);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void updateTrainer_NotFound_ThrowsException() {
        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(1L, trainerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }

    @Test
    void deleteTrainer_Success() {
        when(trainerRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(trainer));

        trainerService.deleteTrainer(1L);

        verify(trainerRepository).delete(trainer);
    }

    @Test
    void deleteTrainer_NotFound_ThrowsException() {
        when(trainerRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.deleteTrainer(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }

    @Test
    void deleteTrainer_WithScheduledClasses_ThrowsException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        trainer.setScheduledClasses(List.of(scheduledClass));

        when(trainerRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(trainer));

        assertThatThrownBy(() -> trainerService.deleteTrainer(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete trainer with scheduled classes");

        verify(trainerRepository, never()).delete(any());
    }

    @Test
    void assignClassTypes_Success() {
        Set<Long> classTypeIds = Set.of(1L);
        List<ClassType> classTypes = List.of(classType);

        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.of(trainer));
        when(classTypeRepository.findAllById(classTypeIds)).thenReturn(classTypes);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponseWithClassTypes(trainer)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.assignClassTypes(1L, classTypeIds);

        assertThat(result).isNotNull();
        verify(trainerRepository).save(trainer);
    }

    @Test
    void assignClassTypes_InvalidClassTypeIds_ThrowsException() {
        Set<Long> classTypeIds = Set.of(1L, 2L);
        List<ClassType> classTypes = List.of(classType);

        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.of(trainer));
        when(classTypeRepository.findAllById(classTypeIds)).thenReturn(classTypes);

        assertThatThrownBy(() -> trainerService.assignClassTypes(1L, classTypeIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void removeClassTypes_Success() {
        trainer.getClassTypes().add(classType);
        Set<Long> classTypeIds = Set.of(1L);

        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toResponseWithClassTypes(trainer)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.removeClassTypes(1L, classTypeIds);

        assertThat(result).isNotNull();
        verify(trainerRepository).save(trainer);
    }

    @Test
    void getTrainersByClassType_Success() {
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponse> responses = List.of(trainerResponse);

        when(classTypeRepository.existsById(1L)).thenReturn(true);
        when(trainerRepository.findByClassTypeId(1L)).thenReturn(trainers);
        when(trainerMapper.toResponseListWithoutCount(trainers)).thenReturn(responses);

        List<TrainerResponse> result = trainerService.getTrainersByClassType(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTrainersByClassType_ClassTypeNotFound_ThrowsException() {
        when(classTypeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> trainerService.getTrainersByClassType(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Class type not found");
    }

    @Test
    void searchTrainersByName_EmptyName_ThrowsException() {
        assertThatThrownBy(() -> trainerService.searchTrainersByName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");
    }
}
