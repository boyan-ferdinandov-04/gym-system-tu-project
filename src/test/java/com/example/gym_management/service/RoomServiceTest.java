package com.example.gym_management.service;

import com.example.gym_management.dto.RoomDTO;
import com.example.gym_management.dto.RoomRequest;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import com.example.gym_management.entity.Room;
import com.example.gym_management.entity.ScheduledClass;
import com.example.gym_management.mapper.RoomMapper;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.RoomRepository;
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
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Gym gym;
    private Room room;
    private RoomRequest roomRequest;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        gym = new Gym("Main Gym", "123 Main St", "555-1234");
        gym.setId(1L);
        gym.setStatus(GymStatus.ACTIVE);

        room = new Room(gym, "Studio A", 20, true);
        room.setId(1L);
        room.setScheduledClasses(new ArrayList<>());

        roomRequest = new RoomRequest(1L, "Studio A", 20, true);

        roomDTO = new RoomDTO(1L, 1L, "Studio A", 20, true);
    }

    @Test
    void createRoom_Success() {
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));
        when(roomRepository.existsByGymIdAndRoomName(1L, "Studio A")).thenReturn(false);
        when(roomMapper.toEntityWithGym(roomRequest, gym)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(roomDTO);

        RoomDTO result = roomService.createRoom(roomRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomName()).isEqualTo("Studio A");
        assertThat(result.getCapacity()).isEqualTo(20);
        assertThat(result.getHasEquipment()).isTrue();

        verify(roomRepository).save(room);
    }

    @Test
    void getRoomById_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(room)).thenReturn(roomDTO);

        RoomDTO result = roomService.getRoomById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(roomRepository).findById(1L);
    }

    @Test
    void getRoomById_NotFound_ThrowsException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void getAllRooms_Success() {
        List<Room> rooms = List.of(room);
        List<RoomDTO> roomDTOs = List.of(roomDTO);

        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toDtoList(rooms)).thenReturn(roomDTOs);

        List<RoomDTO> result = roomService.getAllRooms();

        assertThat(result).hasSize(1);
        verify(roomRepository).findAll();
    }

    @Test
    void getAllRooms_EmptyList() {
        when(roomRepository.findAll()).thenReturn(List.of());
        when(roomMapper.toDtoList(List.of())).thenReturn(List.of());

        List<RoomDTO> result = roomService.getAllRooms();

        assertThat(result).isEmpty();
    }

    @Test
    void updateRoom_Success() {
        RoomRequest updateRequest = new RoomRequest(1L, "Studio B", 30, false);
        RoomDTO updatedDTO = new RoomDTO(1L, 1L, "Studio B", 30, false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByGymIdAndRoomName(1L, "Studio B")).thenReturn(false);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(updatedDTO);

        RoomDTO result = roomService.updateRoom(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRoomName()).isEqualTo("Studio B");
        assertThat(result.getCapacity()).isEqualTo(30);
        assertThat(result.getHasEquipment()).isFalse();

        verify(roomMapper).updateEntity(updateRequest, room);
        verify(roomRepository).save(room);
    }

    @Test
    void updateRoom_NotFound_ThrowsException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateRoom(1L, roomRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void deleteRoom_Success() {
        when(roomRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_NotFound_ThrowsException() {
        when(roomRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.deleteRoom(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void deleteRoom_WithScheduledClasses_ThrowsException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        room.setScheduledClasses(List.of(scheduledClass));

        when(roomRepository.findByIdWithScheduledClasses(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.deleteRoom(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete room with scheduled classes");

        verify(roomRepository, never()).delete(any());
    }
}
