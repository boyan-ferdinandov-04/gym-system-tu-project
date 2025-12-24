package com.example.gym_management.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "room_name", nullable = false, length = 100)
  private String roomName;

  @Column(nullable = false)
  private Integer capacity;

  @Column(name = "has_equipment", nullable = false)
  private Boolean hasEquipment;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
  private List<ScheduledClass> scheduledClasses;

  public Room() {
  }

  public Room(String roomName, Integer capacity, Boolean hasEquipment) {
    this.roomName = roomName;
    this.capacity = capacity;
    this.hasEquipment = hasEquipment;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Boolean getHasEquipment() {
    return hasEquipment;
  }

  public void setHasEquipment(Boolean hasEquipment) {
    this.hasEquipment = hasEquipment;
  }

  public List<ScheduledClass> getScheduledClasses() {
    return scheduledClasses;
  }

  public void setScheduledClasses(List<ScheduledClass> scheduledClasses) {
    this.scheduledClasses = scheduledClasses;
  }
}
