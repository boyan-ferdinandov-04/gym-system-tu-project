package com.example.gym_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gym_id", nullable = false)
  private Gym gym;

  @Column(name = "room_name", nullable = false, length = 100)
  private String roomName;

  @Column(nullable = false)
  private Integer capacity;

  @Column(name = "has_equipment", nullable = false)
  private Boolean hasEquipment;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  public Room(Gym gym, String roomName, Integer capacity, Boolean hasEquipment) {
    this.gym = gym;
    this.roomName = roomName;
    this.capacity = capacity;
    this.hasEquipment = hasEquipment;
  }

  public Room(String roomName, Integer capacity, Boolean hasEquipment) {
    this.roomName = roomName;
    this.capacity = capacity;
    this.hasEquipment = hasEquipment;
  }
}
