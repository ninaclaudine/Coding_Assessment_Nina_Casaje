package com.hitachi.drone.controller;

import com.hitachi.drone.exception.BatterLevelLowException;
import com.hitachi.drone.exception.WeightLimitExceededException;
import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;
import com.hitachi.drone.service.DroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DroneControllerTest {

    @InjectMocks
    private DroneController droneController;

    @Mock
    private DroneService droneService;

    private Drone testDrone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testDrone = new Drone();
        testDrone.setSerialNumber("DRONE001");
        testDrone.setModel(Drone.DroneModel.CRUISERWEIGHT);
        testDrone.setBatteryCapacity(50);
        testDrone.setWeightLimit(1000);
        testDrone.setState(Drone.DroneState.IDLE);
    }

    @Test
    void registerDrone_shouldReturnRegisteredDrone() {
        when(droneService.registerDrone(testDrone)).thenReturn(testDrone);

        ResponseEntity<Drone> response = droneController.registerDrone(testDrone);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testDrone, response.getBody());
        verify(droneService).registerDrone(testDrone);
    }

    @Test
    void loadDrone_shouldReturnUpdatedDrone_whenSuccessful() throws WeightLimitExceededException, BatterLevelLowException {
        String droneId = "DRONE001";
        String medicationId = "MED01";

        when(droneService.loadDrone(droneId, medicationId)).thenReturn(Optional.of(testDrone));

        ResponseEntity<?> response = droneController.loadDrone(droneId, medicationId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testDrone, response.getBody());
        verify(droneService).loadDrone(droneId, medicationId);
    }

    @Test
    void loadDrone_shouldReturnWeightLimitExceededMessage_whenWeightLimitExceeded() throws WeightLimitExceededException, BatterLevelLowException {
        String droneId = "DRONE001";
        String medicationId = "MED01";
        String errorMessage = "Weight limit exceeded";

        when(droneService.loadDrone(droneId, medicationId)).thenThrow(new WeightLimitExceededException(errorMessage));

        ResponseEntity<?> response = droneController.loadDrone(droneId, medicationId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.singletonMap("message", errorMessage), response.getBody());
        verify(droneService).loadDrone(droneId, medicationId);
    }

    @Test
    void loadDrone_shouldThrowRuntimeException_whenBatteryLevelLow() throws WeightLimitExceededException, BatterLevelLowException {
        String droneId = "DRONE001";
        String medicationId = "MED01";

        when(droneService.loadDrone(droneId, medicationId)).thenThrow(new BatterLevelLowException("Battery level is low"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            droneController.loadDrone(droneId, medicationId);
        });

        assertEquals("Battery level is low", exception.getCause().getMessage());
        verify(droneService).loadDrone(droneId, medicationId);
    }

    @Test
    void viewDroneLoad_shouldReturnLoadedMedications() {
        // Assuming Medication is the correct type to be returned
        Medication medication = new Medication();
        medication.setName("Biogesic");
        medication.setWeight(100);
        medication.setCode("MED01");
        medication.setImage("images/pain_relief.png");
        medication.setQuantity(1);

        // Create a list of medications to return
        List<Medication> medications = Collections.singletonList(medication);

        // Mock the service method to return the list of medications
        when(droneService.viewDroneLoadMedication("DRONE001")).thenReturn(medications);

        ResponseEntity<?> response = droneController.viewDroneLoad("DRONE001");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(medications, response.getBody());
        verify(droneService).viewDroneLoadMedication("DRONE001");
    }


    @Test
    void viewDroneStatus_shouldReturnAvailabilityStatus() {
        when(droneService.viewDroneStatus("DRONE001")).thenReturn(true);

        ResponseEntity<?> response = droneController.viewDroneStats("DRONE001");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.singletonMap("is_available_for_loading", true), response.getBody());
        verify(droneService).viewDroneStatus("DRONE001");
    }

    @Test
    void viewDrones_shouldReturnListOfDrones() {
        when(droneService.viewDrones()).thenReturn(Collections.singletonList(testDrone));

        ResponseEntity<List<Drone>> response = droneController.viewDrone();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.singletonList(testDrone), response.getBody());
        verify(droneService).viewDrones();
    }
}
