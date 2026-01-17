package com.example.gym_management.service;

import com.example.gym_management.config.MembershipProperties;
import com.example.gym_management.dto.BookingDTOs.BookingEligibility;
import com.example.gym_management.dto.BookingDTOs.ClassAvailability;
import com.example.gym_management.dto.BookingRequest;
import com.example.gym_management.dto.BookingResponse;
import com.example.gym_management.dto.MemberDTO;
import com.example.gym_management.entity.*;
import com.example.gym_management.entity.Booking.BookingStatus;
import com.example.gym_management.mapper.BookingMapper;
import com.example.gym_management.repository.BookingRepository;
import com.example.gym_management.repository.MemberRepository;
import com.example.gym_management.repository.ScheduledClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ScheduledClassRepository scheduledClassRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private WaitlistService waitlistService;

    @Mock
    private MembershipProperties membershipProperties;

    @InjectMocks
    private BookingService bookingService;

    private Member member;
    private MembershipPlan membershipPlan;
    private ScheduledClass scheduledClass;
    private Room room;
    private Booking booking;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private LocalDateTime futureTime;

    @BeforeEach
    void setUp() {
        futureTime = LocalDateTime.now().plusDays(1);

        membershipPlan = new MembershipPlan("Gold", new BigDecimal("99.99"), 30);
        membershipPlan.setId(1L);

        member = new Member("John", "Doe", "john@example.com", membershipPlan);
        member.setId(1L);
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
        member.setMembershipStartDate(LocalDate.now());
        member.setMembershipEndDate(LocalDate.now().plusDays(30));

        room = new Room("Studio A", 20, true);
        room.setId(1L);

        ClassType classType = new ClassType("Yoga", "Relaxing yoga class");
        classType.setId(1L);

        Trainer trainer = new Trainer("Jane", "Smith");
        trainer.setId(1L);

        scheduledClass = new ScheduledClass(classType, trainer, room, futureTime);
        scheduledClass.setId(1L);

        booking = new Booking(member, scheduledClass, BookingStatus.ENROLLED);
        booking.setId(1L);

        bookingRequest = new BookingRequest(1L, 1L);

        MemberDTO memberDTO = new MemberDTO(1L, "John", "Doe", "john@example.com");
        bookingResponse = new BookingResponse(1L, memberDTO, null, BookingStatus.ENROLLED, futureTime, "Yoga",
                "Jane Smith", "Studio A");

        // Mock membership properties (lenient since not all tests use it)
        lenient().when(membershipProperties.getCancellationDeadlineHours()).thenReturn(1);
    }

    @Test
    void createBooking_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(false);
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(5L);
        when(bookingMapper.toEntity(member, scheduledClass)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.createBooking(bookingRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBooking_MemberNotFound_ThrowsException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    void createBooking_ClassFullyBooked_ThrowsException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(false);
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(20L);

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fully booked");
    }

    @Test
    void createBooking_AlreadyEnrolled_ThrowsException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already enrolled");
    }

    @Test
    void getBookingById_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponse(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getBookingById_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void getAllBookings_Success() {
        List<Booking> bookings = List.of(booking);
        List<BookingResponse> responses = List.of(bookingResponse);

        when(bookingRepository.findAll()).thenReturn(bookings);
        when(bookingMapper.toResponseList(bookings)).thenReturn(responses);

        List<BookingResponse> result = bookingService.getAllBookings();

        assertThat(result).hasSize(1);
    }

    @Test
    void cancelBooking_Success() {
        scheduledClass.setStartTime(LocalDateTime.now().plusHours(3));
        BookingResponse cancelledResponse = new BookingResponse(1L, null, null, BookingStatus.CANCELLED, null, null,
                null, null);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(cancelledResponse);

        BookingResponse result = bookingService.cancelBooking(1L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_AlreadyCancelled_ThrowsException() {
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void cancelBooking_WithinOneHour_ThrowsException() {
        scheduledClass.setStartTime(LocalDateTime.now().plusMinutes(30));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("within 1 hour");
    }

    @Test
    void reEnrollBooking_Success() {
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(false);
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(5L);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.reEnrollBooking(1L);

        assertThat(result).isNotNull();
        verify(bookingRepository).save(booking);
    }

    @Test
    void deleteBooking_Success() {
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void deleteBooking_ActiveBooking_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.deleteBooking(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete an active booking");

        verify(bookingRepository, never()).delete(any());
    }

    @Test
    void getBookingsByMember_Success() {
        List<Booking> bookings = List.of(booking);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookingRepository.findByMemberId(1L)).thenReturn(bookings);
        when(bookingMapper.toCompactResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getBookingsByMember(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUpcomingBookingsByMember_Success() {
        List<Booking> bookings = List.of(booking);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookingRepository.findUpcomingBookingsByMemberId(eq(1L), any(LocalDateTime.class))).thenReturn(bookings);
        when(bookingMapper.toCompactResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUpcomingBookingsByMember(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByScheduledClass_Success() {
        List<Booking> bookings = List.of(booking);
        List<BookingResponse> responses = List.of(bookingResponse);

        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.findByScheduledClassId(1L)).thenReturn(bookings);
        when(bookingMapper.toResponseList(bookings)).thenReturn(responses);

        List<BookingResponse> result = bookingService.getBookingsByScheduledClass(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getClassAvailability_Success() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(5L);

        ClassAvailability result = bookingService.getClassAvailability(1L);

        assertThat(result).isNotNull();
        assertThat(result.totalCapacity()).isEqualTo(20);
        assertThat(result.currentEnrollments()).isEqualTo(5);
        assertThat(result.availableSpots()).isEqualTo(15);
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void checkBookingEligibility_Eligible() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(false);
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(5L);

        BookingEligibility result = bookingService.checkBookingEligibility(1L, 1L);

        assertThat(result.eligible()).isTrue();
    }

    @Test
    void checkBookingEligibility_ClassFull_NotEligible() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.existsByMemberIdAndScheduledClassIdAndStatus(1L, 1L, BookingStatus.ENROLLED))
                .thenReturn(false);
        when(bookingRepository.countEnrolledByScheduledClassId(1L)).thenReturn(20L);

        BookingEligibility result = bookingService.checkBookingEligibility(1L, 1L);

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("fully booked");
    }

    @Test
    void cancelAllBookingsForClass_Success() {
        Booking booking1 = new Booking(member, scheduledClass, BookingStatus.ENROLLED);
        booking1.setId(1L);
        Booking booking2 = new Booking(member, scheduledClass, BookingStatus.ENROLLED);
        booking2.setId(2L);

        List<Booking> bookings = new ArrayList<>(List.of(booking1, booking2));

        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(bookingRepository.findByScheduledClassId(1L)).thenReturn(bookings);

        int result = bookingService.cancelAllBookingsForClass(1L);

        assertThat(result).isEqualTo(2);
        verify(bookingRepository).saveAll(anyList());
    }

    @Test
    void getBookingsByStatus_Success() {
        List<Booking> bookings = List.of(booking);
        List<BookingResponse> responses = List.of(bookingResponse);

        when(bookingRepository.findByStatus(BookingStatus.ENROLLED)).thenReturn(bookings);
        when(bookingMapper.toResponseList(bookings)).thenReturn(responses);

        List<BookingResponse> result = bookingService.getBookingsByStatus(BookingStatus.ENROLLED);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Booking> bookings = List.of(booking);
        List<BookingResponse> responses = List.of(bookingResponse);

        when(bookingRepository.findBookingsByDateRange(startDate, endDate)).thenReturn(bookings);
        when(bookingMapper.toResponseList(bookings)).thenReturn(responses);

        List<BookingResponse> result = bookingService.getBookingsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByDateRange_InvalidRange_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);

        assertThatThrownBy(() -> bookingService.getBookingsByDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before end date");
    }
}
