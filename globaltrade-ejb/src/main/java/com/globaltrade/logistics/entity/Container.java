package com.globaltrade.logistics.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "containers")
public class Container implements Serializable {

    public enum ContainerType {
        STANDARD_20FT, STANDARD_40FT, REFRIGERATED, FLAT_RACK, OPEN_TOP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "container_number", nullable = false, unique = true, length = 50)
    private String containerNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContainerType type;

    @Column(name = "current_location", length = 100)
    private String currentLocation;

    public Container() {}

    public Long getId() { return id; }
    public String getContainerNumber() { return containerNumber; }
    public void setContainerNumber(String containerNumber) { this.containerNumber = containerNumber; }
    public ContainerType getType() { return type; }
    public void setType(ContainerType type) { this.type = type; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
}