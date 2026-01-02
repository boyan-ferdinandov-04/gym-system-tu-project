package com.example.gym_management.repository;

import com.example.gym_management.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findByMembershipPlanId(Long membershipPlanId);

    @Query("SELECT m FROM Member m WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Member> searchByName(@Param("name") String name);

    @Query("SELECT m FROM Member m WHERE m.membershipPlan IS NULL")
    List<Member> findMembersWithoutPlan();

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.bookings WHERE m.id = :id")
    Optional<Member> findByIdWithBookings(@Param("id") Long id);
}
