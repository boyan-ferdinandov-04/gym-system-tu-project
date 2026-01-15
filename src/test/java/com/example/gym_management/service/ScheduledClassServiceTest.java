package com.example.gym_management.service;

import com.example.gym_management.dto.*;
import com.example.gym_management.entity.*;
import com.example.gym_management.mapper.ScheduledClassMapper;
import com.example.gym_management.repository.ClassTypeRepository;
import com.example.gym_management.repository.RoomRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import com.example.gym_management.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledClassServiceTest {

    @Mock
    private ScheduledClassRepository scheduledClassRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ScheduledClassMapper scheduledClassMapper;

    @Mock
    private TrainerAvailabilityService trainerAvailabilityService;

    @InjectMocks
    private ScheduledClassService scheduledClassService;

    private ScheduledClass scheduledClass;
    private ScheduledClassRequest request;
    private ScheduledClassResponse response;
    private ClassType classType;
    private Trainer trainer;
    private Room room;
    private LocalDateTime futureTime;

    @BeforeEach
    void setUp() {
        futureTime = LocalDateTime.now().plusDays(1);

        classType = new ClassType("Yoga", "Relaxing yoga class");
        classType.setId(1L);

        trainer = new Trainer("Jane", "Smith");
        trainer.setId(1L);

        room = new Room("Studio A", 20, true);
        room.setId(1L);

        scheduledClass = new ScheduledClass(classType, trainer, room, futureTime);
        scheduledClass.setId(1L);
        scheduledClass.setBookings(new ArrayList<>());

        request = new ScheduledClassRequest(1L, 1L, 1L, futureTime);

        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "Yoga", "Relaxing yoga class");
        TrainerDTO trainerDTO = new TrainerDTO(1L, "Jane", "Smith");
        RoomDTO roomDTO = new RoomDTO(1L, "Studio A", 20, true);
        response = new ScheduledClassResponse(1L, classTypeDTO, trainerDTO, roomDTO, futureTime, 0, 20);
    }

    @Test
    void createScheduledClass_Success() {
        when(scheduledClassRepository.findByTrainerIdAndTimeRange(eq(1L), any(), any())).thenReturn(List.of());
        when(scheduledClassRepository.findByRoomIdAndTimeRange(eq(1L), any(), any())).thenReturn(List.of());
        when(scheduledClassMapper.toEntity(request)).thenReturn(scheduledClass);
        when(scheduledClassRepository.save(scheduledClass)).thenReturn(scheduledClass);
        when(scheduledClassMapper.toResponse(scheduledClass)).thenReturn(response);

        ScheduledClassResponse result = scheduledClassService.createScheduledClass(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(trainerAvailabilityService).validateTrainerAvailabilityForClass(1L, futureTime);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    void createScheduledClass_TrainerConflict_ThrowsException() {
        when(scheduledClassRepository.findByTrainerIdAndTimeRange(eq(1L), any(), any()))
                .thenReturn(List.of(scheduledClass));

        assertThatThrownBy(() -> scheduledClassService.createScheduledClass(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Trainer is already scheduled");
    }

    @Test
    void createScheduledClass_RoomConflict_ThrowsException() {
        when(scheduledClassRepository.findByTrainerIdAndTimeRange(eq(1L), any(), any())).thenReturn(List.of());
        when(scheduledClassRepository.findByRoomIdAndTimeRange(eq(1L), any(), any()))
                .thenReturn(List.of(scheduledClass));

        assertThatThrownBy(() -> scheduledClassService.createScheduledClass(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Room is already occupied");
    }

    @Test
    void getScheduledClassById_Success() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(scheduledClassMapper.toResponse(scheduledClass)).thenReturn(response);

        ScheduledClassResponse result = scheduledClassService.getScheduledClassById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getScheduledClassById_NotFound_ThrowsException() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduledClassService.getScheduledClassById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scheduled class not found");
    }

    @Test
    void getAllScheduledClasses_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(scheduledClassRepository.findAll()).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getAllScheduledClasses();

        assertThat(result).hasSize(1);
    }

    @Test
    void getUpcomingClasses_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(scheduledClassRepository.findUpcomingClasses(any(LocalDateTime.class))).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getUpcomingClasses();

        assertThat(result).hasSize(1);
    }

    @Test
    void getClassesByTrainer_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(scheduledClassRepository.findByTrainerId(1L)).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getClassesByTrainer(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getClassesByRoom_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(roomRepository.existsById(1L)).thenReturn(true);
        when(scheduledClassRepository.findByRoomId(1L)).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getClassesByRoom(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAvailableClasses_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);

        when(scheduledClassRepository.findUpcomingClasses(any(LocalDateTime.class))).thenReturn(classes);
        when(scheduledClassMapper.toResponse(scheduledClass)).thenReturn(response);

        List<ScheduledClassResponse> result = scheduledClassService.getAvailableClasses();

        assertThat(result).hasSize(1);
    }

    @Test
    void updateScheduledClass_Success() {
        ScheduledClassRequest updateRequest = new ScheduledClassRequest(1L, 1L, 1L, futureTime.plusHours(2));
        ScheduledClassResponse updatedResponse = new ScheduledClassResponse(1L, null, null, null,
                futureTime.plusHours(2), 0, 20);

        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(scheduledClassRepository.findByTrainerIdAndTimeRange(eq(1L), any(), any())).thenReturn(List.of());
        when(scheduledClassRepository.findByRoomIdAndTimeRange(eq(1L), any(), any())).thenReturn(List.of());
        when(scheduledClassRepository.save(scheduledClass)).thenReturn(scheduledClass);
        when(scheduledClassMapper.toResponse(scheduledClass)).thenReturn(updatedResponse);

        ScheduledClassResponse result = scheduledClassService.updateScheduledClass(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(scheduledClassMapper).updateEntity(updateRequest, scheduledClass);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    void updateScheduledClass_NotFound_ThrowsException() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduledClassService.updateScheduledClass(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scheduled class not found");
    }

    @Test
    void deleteScheduledClass_Success() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));

        scheduledClassService.deleteScheduledClass(1L);

        verify(scheduledClassRepository).delete(scheduledClass);
    }

    @Test
    void deleteScheduledClass_NotFound_ThrowsException() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduledClassService.deleteScheduledClass(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scheduled class not found");
    }

    @Test
    void deleteScheduledClass_WithBookings_ThrowsException() {
        Booking booking = new Booking();
        scheduledClass.setBookings(List.of(booking));

        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));

        assertThatThrownBy(() -> scheduledClassService.deleteScheduledClass(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete scheduled class with existing bookings");

        verify(scheduledClassRepository, never()).delete(any());
    }

    @Test
    void getClassesByType_Success() {
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(classTypeRepository.existsById(1L)).thenReturn(true);
        when(scheduledClassRepository.findByClassTypeId(1L)).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getClassesByType(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getClassesByDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<ScheduledClass> classes = List.of(scheduledClass);
        List<ScheduledClassResponse> responses = List.of(response);

        when(scheduledClassRepository.findByStartTimeBetween(startDate, endDate)).thenReturn(classes);
        when(scheduledClassMapper.toResponseList(classes)).thenReturn(responses);

        List<ScheduledClassResponse> result = scheduledClassService.getClassesByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void getClassesByDateRange_InvalidRange_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);

        assertThatThrownBy(() -> scheduledClassService.getClassesByDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before end date");
    }
}
