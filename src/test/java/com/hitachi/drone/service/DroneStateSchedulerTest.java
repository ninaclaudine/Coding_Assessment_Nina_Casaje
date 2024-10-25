package com.hitachi.drone.service;

import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Drone.DroneState;
import com.hitachi.drone.repository.DroneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DroneStateSchedulerTest {

    @InjectMocks
    private DroneStateScheduler droneStateScheduler;

    @Mock
    private DroneRepository droneRepository;

    private Drone drone1;
    private Drone drone2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test drones
        drone1 = new Drone();
        drone1.setSerialNumber("DRONE001");
        drone1.setBatteryCapacity(50);
        drone1.setState(DroneState.IDLE);

        drone2 = new Drone();
        drone2.setSerialNumber("DRONE002");
        drone2.setBatteryCapacity(10);
        drone2.setState(DroneState.DELIVERING);
    }

    @Test
    void updateDroneStates_shouldHandleIdleState() {
        when(droneRepository.findAll()).thenReturn(List.of(drone1));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone1);
        assertEquals(DroneState.IDLE, drone1.getState());
    }

    @Test
    void updateDroneStates_shouldHandleLoadingToLoaded() {
        drone1.setState(DroneState.LOADING);
        when(droneRepository.findAll()).thenReturn(List.of(drone1));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone1);
        assertEquals(DroneState.LOADED, drone1.getState());
    }

    @Test
    void updateDroneStates_shouldHandleLoadedToDelivering() {
        drone1.setState(DroneState.LOADED);
        when(droneRepository.findAll()).thenReturn(List.of(drone1));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone1);
        assertEquals(DroneState.DELIVERING, drone1.getState());
    }

    @Test
    void updateDroneStates_shouldHandleDeliveringToDelivered() {
        when(droneRepository.findAll()).thenReturn(List.of(drone2));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone2);
        assertEquals(DroneState.DELIVERED, drone2.getState());
        assertEquals(0, drone2.getBatteryCapacity()); // Check battery doesn't go below zero
    }

    @Test
    void updateDroneStates_shouldHandleDeliveredToReturning() {
        drone1.setState(DroneState.DELIVERED);
        when(droneRepository.findAll()).thenReturn(List.of(drone1));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone1);
        assertEquals(DroneState.RETURNING, drone1.getState());
    }

    @Test
    void updateDroneStates_shouldHandleReturningToIdle() {
        drone1.setState(DroneState.RETURNING);
        when(droneRepository.findAll()).thenReturn(List.of(drone1));

        droneStateScheduler.updateDroneStates();

        verify(droneRepository).save(drone1);
        assertEquals(DroneState.IDLE, drone1.getState());
    }

}
