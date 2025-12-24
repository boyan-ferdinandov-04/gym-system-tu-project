package com.example.gym_management.repository;

import com.example.gym_management.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

  List<MembershipPlan> findByTierNameContainingIgnoreCase(String tierName);

  List<MembershipPlan> findByDurationDaysLessThanEqual(Integer durationDays);
}
