package com.example.gym_management.service;

import com.example.gym_management.dto.*;
import com.example.gym_management.entity.Trainer;
import com.example.gym_management.entity.TrainerAvailability;
import com.example.gym_management.entity.TrainerTimeOff;
import com.example.gym_management.mapper.TrainerAvailabilityMapper;
import com.example.gym_management.repository.TrainerAvailabilityRepository;
import com.example.gym_management.repository.TrainerRepository;
import com.example.gym_management.repository.TrainerTimeOffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerAvailabilityServiceTest {

    @Mock
    private TrainerAvailabilityRepository availabilityRepository;

    @Mock
    private TrainerTimeOffRepository timeOffRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerAvailabilityMapper availabilityMapper;

    @InjectMocks
    private TrainerAvailabilityService trainerAvailabilityService;

    private Trainer trainer;
    private TrainerAvailability availability;
    private TrainerAvailabilityRequest availabilityRequest;
    private TrainerAvailabilityResponse availabilityResponse;
    private TrainerTimeOff timeOff;
    private TrainerTimeOffRequest timeOffRequest;
    private TrainerTimeOffResponse timeOffResponse;

    @BeforeEach
    void setUp() {
        trainer = new Trainer("Jane", "Smith");
        trainer.setId(1L);

        availability = new TrainerAvailability(trainer, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        availability.setId(1L);

        availabilityRequest = new TrainerAvailabilityRequest(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

        availabilityResponse = new TrainerAvailabilityResponse(1L, 1L, "Jane Smith", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));

        timeOff = new TrainerTimeOff(trainer, LocalDate.now().plusDays(7), "Vacation");
        timeOff.setId(1L);

        timeOffRequest = new TrainerTimeOffRequest(LocalDate.now().plusDays(7), "Vacation");

        timeOffResponse = new TrainerTimeOffResponse(1L, 1L, "Jane Smith", LocalDate.now().plusDays(7), "Vacation");
    }

    @Test
    void createAvailability_Success() {
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(availabilityRepository.findOverlappingAvailability(eq(1L), eq(DayOfWeek.MONDAY), any(), any()))
                .thenReturn(List.of());
        when(availabilityMapper.toAvailabilityEntity(availabilityRequest, 1L)).thenReturn(availability);
        when(availabilityRepository.save(availability)).thenReturn(availability);
        when(availabilityMapper.toAvailabilityResponse(availability)).thenReturn(availabilityResponse);

        TrainerAvailabilityResponse result = trainerAvailabilityService.createAvailability(1L, availabilityRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        verify(availabilityRepository).save(availability);
    }

    @Test
    void createAvailability_TrainerNotFound_ThrowsException() {
        when(trainerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> trainerAvailabilityService.createAvailability(1L, availabilityRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found");
    }

    @Test
    void createAvailability_InvalidTimeRange_ThrowsException() {
        TrainerAvailabilityRequest invalidRequest = new TrainerAvailabilityRequest(DayOfWeek.MONDAY,
                LocalTime.of(17, 0), LocalTime.of(9, 0));

        when(trainerRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> trainerAvailabilityService.createAvailability(1L, invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start time must be before end time");
    }

    @Test
    void createAvailability_Overlap_ThrowsException() {
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(availabilityRepository.findOverlappingAvailability(eq(1L), eq(DayOfWeek.MONDAY), any(), any()))
                .thenReturn(List.of(availability));

        assertThatThrownBy(() -> trainerAvailabilityService.createAvailability(1L, availabilityRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("overlaps");
    }

    @Test
    void getTrainerAvailability_Success() {
        List<TrainerAvailability> availabilities = List.of(availability);
        List<TrainerAvailabilityResponse> responses = List.of(availabilityResponse);

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(availabilityRepository.findByTrainerId(1L)).thenReturn(availabilities);
        when(availabilityMapper.toAvailabilityResponseList(availabilities)).thenReturn(responses);

        List<TrainerAvailabilityResponse> result = trainerAvailabilityService.getTrainerAvailability(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void updateAvailability_Success() {
        TrainerAvailabilityRequest updateRequest = new TrainerAvailabilityRequest(DayOfWeek.TUESDAY,
                LocalTime.of(10, 0), LocalTime.of(18, 0));
        TrainerAvailabilityResponse updatedResponse = new TrainerAvailabilityResponse(1L, 1L, "Jane Smith",
                DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0));

        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));
        when(availabilityRepository.findOverlappingAvailability(eq(1L), eq(DayOfWeek.TUESDAY), any(), any()))
                .thenReturn(List.of());
        when(availabilityRepository.save(availability)).thenReturn(availability);
        when(availabilityMapper.toAvailabilityResponse(availability)).thenReturn(updatedResponse);

        TrainerAvailabilityResponse result = trainerAvailabilityService.updateAvailability(1L, 1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        verify(availabilityMapper).updateAvailabilityEntity(updateRequest, availability);
    }

    @Test
    void updateAvailability_NotFound_ThrowsException() {
        when(availabilityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerAvailabilityService.updateAvailability(1L, 1L, availabilityRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Availability slot not found");
    }

    @Test
    void deleteAvailability_Success() {
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        trainerAvailabilityService.deleteAvailability(1L, 1L);

        verify(availabilityRepository).delete(availability);
    }

    @Test
    void createTimeOff_Success() {
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, timeOffRequest.getDate())).thenReturn(false);
        when(availabilityMapper.toTimeOffEntity(timeOffRequest, 1L)).thenReturn(timeOff);
        when(timeOffRepository.save(timeOff)).thenReturn(timeOff);
        when(availabilityMapper.toTimeOffResponse(timeOff)).thenReturn(timeOffResponse);

        TrainerTimeOffResponse result = trainerAvailabilityService.createTimeOff(1L, timeOffRequest);

        assertThat(result).isNotNull();
        assertThat(result.getReason()).isEqualTo("Vacation");
        verify(timeOffRepository).save(timeOff);
    }

    @Test
    void createTimeOff_AlreadyExists_ThrowsException() {
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, timeOffRequest.getDate())).thenReturn(true);

        assertThatThrownBy(() -> trainerAvailabilityService.createTimeOff(1L, timeOffRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getTrainerTimeOff_Success() {
        List<TrainerTimeOff> timeOffs = List.of(timeOff);
        List<TrainerTimeOffResponse> responses = List.of(timeOffResponse);

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.findByTrainerId(1L)).thenReturn(timeOffs);
        when(availabilityMapper.toTimeOffResponseList(timeOffs)).thenReturn(responses);

        List<TrainerTimeOffResponse> result = trainerAvailabilityService.getTrainerTimeOff(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void deleteTimeOff_Success() {
        when(timeOffRepository.findById(1L)).thenReturn(Optional.of(timeOff));

        trainerAvailabilityService.deleteTimeOff(1L, 1L);

        verify(timeOffRepository).delete(timeOff);
    }

    @Test
    void isTrainerAvailable_Available_ReturnsTrue() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        LocalDate date = dateTime.toLocalDate();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, date)).thenReturn(false);
        when(availabilityRepository.findByTrainerIdAndDayOfWeekAndTimeWithin(1L, dayOfWeek, time))
                .thenReturn(Optional.of(availability));

        boolean result = trainerAvailabilityService.isTrainerAvailable(1L, dateTime);

        assertThat(result).isTrue();
    }

    @Test
    void isTrainerAvailable_HasTimeOff_ReturnsFalse() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDate date = dateTime.toLocalDate();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, date)).thenReturn(true);

        boolean result = trainerAvailabilityService.isTrainerAvailable(1L, dateTime);

        assertThat(result).isFalse();
    }

    @Test
    void findAvailableTrainers_Success() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        LocalTime time = LocalTime.of(10, 0);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        AvailableTrainerDTO availableTrainerDTO = new AvailableTrainerDTO(1L, "Jane", "Smith",
                LocalTime.of(9, 0), LocalTime.of(17, 0), List.of());

        when(timeOffRepository.findTrainerIdsWithTimeOffOnDate(date)).thenReturn(List.of());
        when(availabilityRepository.findAvailableTrainersByDayAndTime(dayOfWeek, time))
                .thenReturn(List.of(availability));
        when(trainerRepository.findByIdWithClassTypes(1L)).thenReturn(Optional.of(trainer));
        when(availabilityMapper.toAvailableTrainerDTO(trainer, availability)).thenReturn(availableTrainerDTO);

        List<AvailableTrainerDTO> result = trainerAvailabilityService.findAvailableTrainers(date, time);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void validateTrainerAvailabilityForClass_Available_Success() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        LocalTime time = startTime.toLocalTime();
        LocalDate date = startTime.toLocalDate();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, date)).thenReturn(false);
        when(availabilityRepository.findByTrainerIdAndDayOfWeekAndTimeWithin(1L, dayOfWeek, time))
                .thenReturn(Optional.of(availability));

        trainerAvailabilityService.validateTrainerAvailabilityForClass(1L, startTime);

        verify(availabilityRepository).findByTrainerIdAndDayOfWeekAndTimeWithin(1L, dayOfWeek, time);
    }

    @Test
    void validateTrainerAvailabilityForClass_HasTimeOff_ThrowsException() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDate date = startTime.toLocalDate();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, date)).thenReturn(true);

        assertThatThrownBy(() -> trainerAvailabilityService.validateTrainerAvailabilityForClass(1L, startTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("time off");
    }

    @Test
    void validateTrainerAvailabilityForClass_NotAvailable_ThrowsException() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        LocalTime time = startTime.toLocalTime();
        LocalDate date = startTime.toLocalDate();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(timeOffRepository.existsByTrainerIdAndDate(1L, date)).thenReturn(false);
        when(availabilityRepository.findByTrainerIdAndDayOfWeekAndTimeWithin(1L, dayOfWeek, time))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerAvailabilityService.validateTrainerAvailabilityForClass(1L, startTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }
}
