package com.example.gym_management.service;

import com.example.gym_management.dto.GymDTO;
import com.example.gym_management.dto.MembershipPlanRequest;
import com.example.gym_management.dto.MembershipPlanResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.GymStatus;
import com.example.gym_management.entity.Member;
import com.example.gym_management.entity.MembershipPlan;
import com.example.gym_management.mapper.MembershipPlanMapper;
import com.example.gym_management.repository.MembershipPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @Mock
    private MembershipPlanMapper membershipPlanMapper;

    @InjectMocks
    private MembershipService membershipService;

    private Gym gym;
    private MembershipPlan membershipPlan;
    private MembershipPlanRequest request;
    private MembershipPlanResponse response;

    @BeforeEach
    void setUp() {
        gym = new Gym("Main Gym", "123 Main St", "555-1234");
        gym.setId(1L);
        gym.setStatus(GymStatus.ACTIVE);

        Set<Gym> accessibleGyms = new HashSet<>();
        accessibleGyms.add(gym);

        membershipPlan = new MembershipPlan("Gold", new BigDecimal("99.99"), 30, accessibleGyms);
        membershipPlan.setId(1L);
        membershipPlan.setMembers(new ArrayList<>());

        request = new MembershipPlanRequest("Gold", new BigDecimal("99.99"), 30, Set.of(1L));

        GymDTO gymDTO = new GymDTO(1L, "Main Gym");
        response = new MembershipPlanResponse(1L, "Gold", new BigDecimal("99.99"), 30, List.of(gymDTO));
    }

    @Test
    void createMembershipPlan_Success() {
        when(membershipPlanMapper.toEntity(request)).thenReturn(membershipPlan);
        when(membershipPlanRepository.save(membershipPlan)).thenReturn(membershipPlan);
        when(membershipPlanMapper.toResponse(membershipPlan)).thenReturn(response);

        MembershipPlanResponse result = membershipService.createMembershipPlan(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTierName()).isEqualTo("Gold");

        verify(membershipPlanRepository).save(membershipPlan);
    }

    @Test
    void getMembershipPlanById_Success() {
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(membershipPlan));
        when(membershipPlanMapper.toResponse(membershipPlan)).thenReturn(response);

        MembershipPlanResponse result = membershipService.getMembershipPlanById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(membershipPlanRepository).findById(1L);
    }

    @Test
    void getMembershipPlanById_NotFound_ThrowsException() {
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membershipService.getMembershipPlanById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Membership plan not found");
    }

    @Test
    void getAllMembershipPlans_Success() {
        List<MembershipPlan> plans = List.of(membershipPlan);
        List<MembershipPlanResponse> responses = List.of(response);

        when(membershipPlanRepository.findAll()).thenReturn(plans);
        when(membershipPlanMapper.toResponseList(plans)).thenReturn(responses);

        List<MembershipPlanResponse> result = membershipService.getAllMembershipPlans();

        assertThat(result).hasSize(1);
        verify(membershipPlanRepository).findAll();
    }

    @Test
    void getAvailableMembershipPlans_Success() {
        List<MembershipPlan> plans = List.of(membershipPlan);
        List<MembershipPlanResponse> responses = List.of(response);

        when(membershipPlanRepository.findAll()).thenReturn(plans);
        when(membershipPlanMapper.toResponseList(plans)).thenReturn(responses);

        List<MembershipPlanResponse> result = membershipService.getAvailableMembershipPlans();

        assertThat(result).hasSize(1);
    }

    @Test
    void searchMembershipPlansByTierName_Success() {
        List<MembershipPlan> plans = List.of(membershipPlan);
        List<MembershipPlanResponse> responses = List.of(response);

        when(membershipPlanRepository.findByTierNameContainingIgnoreCase("Gold")).thenReturn(plans);
        when(membershipPlanMapper.toResponseList(plans)).thenReturn(responses);

        List<MembershipPlanResponse> result = membershipService.searchMembershipPlansByTierName("Gold");

        assertThat(result).hasSize(1);
        verify(membershipPlanRepository).findByTierNameContainingIgnoreCase("Gold");
    }

    @Test
    void searchMembershipPlansByTierName_NullName_ThrowsException() {
        assertThatThrownBy(() -> membershipService.searchMembershipPlansByTierName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void searchMembershipPlansByTierName_EmptyName_ThrowsException() {
        assertThatThrownBy(() -> membershipService.searchMembershipPlansByTierName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void searchMembershipPlansByTierName_WhitespaceName_ThrowsException() {
        assertThatThrownBy(() -> membershipService.searchMembershipPlansByTierName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void getMembershipPlansByMaxDuration_Success() {
        List<MembershipPlan> plans = List.of(membershipPlan);
        List<MembershipPlanResponse> responses = List.of(response);

        when(membershipPlanRepository.findByDurationDaysLessThanEqual(30)).thenReturn(plans);
        when(membershipPlanMapper.toResponseList(plans)).thenReturn(responses);

        List<MembershipPlanResponse> result = membershipService.getMembershipPlansByMaxDuration(30);

        assertThat(result).hasSize(1);
        verify(membershipPlanRepository).findByDurationDaysLessThanEqual(30);
    }

    @Test
    void getMembershipPlansByMaxDuration_NullDuration_ThrowsException() {
        assertThatThrownBy(() -> membershipService.getMembershipPlansByMaxDuration(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be a positive integer");
    }

    @Test
    void getMembershipPlansByMaxDuration_ZeroDuration_ThrowsException() {
        assertThatThrownBy(() -> membershipService.getMembershipPlansByMaxDuration(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be a positive integer");
    }

    @Test
    void getMembershipPlansByMaxDuration_NegativeDuration_ThrowsException() {
        assertThatThrownBy(() -> membershipService.getMembershipPlansByMaxDuration(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be a positive integer");
    }

    @Test
    void updateMembershipPlan_Success() {
        MembershipPlanRequest updateRequest = new MembershipPlanRequest("Platinum", new BigDecimal("149.99"), 60, Set.of(1L));
        GymDTO gymDTO = new GymDTO(1L, "Main Gym");
        MembershipPlanResponse updatedResponse = new MembershipPlanResponse(1L, "Platinum", new BigDecimal("149.99"), 60, List.of(gymDTO));

        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(membershipPlan));
        when(membershipPlanRepository.save(membershipPlan)).thenReturn(membershipPlan);
        when(membershipPlanMapper.toResponse(membershipPlan)).thenReturn(updatedResponse);

        MembershipPlanResponse result = membershipService.updateMembershipPlan(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTierName()).isEqualTo("Platinum");

        verify(membershipPlanMapper).updateEntity(updateRequest, membershipPlan);
        verify(membershipPlanRepository).save(membershipPlan);
    }

    @Test
    void updateMembershipPlan_NotFound_ThrowsException() {
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membershipService.updateMembershipPlan(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Membership plan not found");
    }

    @Test
    void deleteMembershipPlan_Success() {
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(membershipPlan));

        membershipService.deleteMembershipPlan(1L);

        verify(membershipPlanRepository).delete(membershipPlan);
    }

    @Test
    void deleteMembershipPlan_NotFound_ThrowsException() {
        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membershipService.deleteMembershipPlan(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Membership plan not found");
    }

    @Test
    void deleteMembershipPlan_WithActiveMembers_ThrowsException() {
        Member member = new Member("John", "Doe", "john@example.com", membershipPlan);
        membershipPlan.setMembers(List.of(member));

        when(membershipPlanRepository.findById(1L)).thenReturn(Optional.of(membershipPlan));

        assertThatThrownBy(() -> membershipService.deleteMembershipPlan(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete membership plan with active members");

        verify(membershipPlanRepository, never()).delete(any());
    }
}
