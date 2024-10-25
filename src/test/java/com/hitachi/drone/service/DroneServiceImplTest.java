package com.hitachi.drone.service;

import com.hitachi.drone.exception.BatterLevelLowException;
import com.hitachi.drone.exception.WeightLimitExceededException;
import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;
import com.hitachi.drone.repository.DroneRepository;
import com.hitachi.drone.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DroneServiceImplTest {

    @InjectMocks
    private DroneServiceImpl droneService;

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    private Drone drone;
    private Medication medication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test objects
        drone = new Drone();
        drone.setSerialNumber("DRONE001");
        drone.setWeightLimit(1000);
        drone.setBatteryCapacity(50);
        drone.setState(Drone.DroneState.IDLE); // Ensure the state is initialized
        drone.setMedications(new ArrayList<>());

        medication = new Medication();
        medication.setCode("MED001");
        medication.setName("Pain Reliever");
        medication.setWeight(200);
        medication.setQuantity(1);
    }

    @Test
    void registerDrone_shouldRegisterNewDrone() {
        when(droneRepository.existsById(drone.getSerialNumber())).thenReturn(false);
        when(droneRepository.save(drone)).thenReturn(drone);

        Drone savedDrone = droneService.registerDrone(drone);

        assertNotNull(savedDrone);
        assertEquals("DRONE001", savedDrone.getSerialNumber());
        verify(droneRepository).save(drone);
    }

    @Test
    void registerDrone_shouldThrowExceptionWhenDroneExists() {
        when(droneRepository.existsById(drone.getSerialNumber())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            droneService.registerDrone(drone);
        });

        assertEquals("Drone with this serial number already exists.", exception.getMessage());
        verify(droneRepository, never()).save(drone);
    }

    @Test
    void loadDrone_shouldLoadMedication() throws WeightLimitExceededException, BatterLevelLowException {
        when(droneRepository.findById(drone.getSerialNumber())).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        Drone updatedDrone = droneService.loadDrone(drone.getSerialNumber(), "1").orElse(null);

        assertNotNull(updatedDrone);
        assertEquals(1, updatedDrone.getMedications().size());
        assertEquals(medication.getName(), updatedDrone.getMedications().get(0).getName());
        verify(droneRepository).save(updatedDrone);
    }

    @Test
    void loadDrone_shouldThrowExceptionWhenWeightLimitExceeded() {
        medication.setWeight(900); // Setting weight to exceed the limit
        drone.getMedications().add(medication); // Load existing medication
        when(droneRepository.findById(drone.getSerialNumber())).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        Exception exception = assertThrows(WeightLimitExceededException.class, () -> {
            droneService.loadDrone(drone.getSerialNumber(), "1");
        });

        assertEquals("Total weight exceeds the drone's weight limit.", exception.getMessage());
    }

    @Test
    void loadDrone_shouldThrowExceptionWhenBatteryLow() {
        drone.setBatteryCapacity(20); // Set battery capacity low
        when(droneRepository.findById(drone.getSerialNumber())).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        Exception exception = assertThrows(BatterLevelLowException.class, () -> {
            droneService.loadDrone(drone.getSerialNumber(), "1");
        });

        assertEquals("Battery is below 25% cannot enter LOADING State. ", exception.getMessage());
    }

    @Test
    void viewDroneLoadMedication_shouldReturnMedications() {
        drone.getMedications().add(medication);
        when(droneRepository.findById(drone.getSerialNumber())).thenReturn(Optional.of(drone));

        List<Medication> medications = droneService.viewDroneLoadMedication(drone.getSerialNumber());

        assertEquals(1, medications.size());
        assertEquals(medication.getName(), medications.get(0).getName());
    }

    @Test
    void viewDroneStatus_shouldReturnTrueIfUnderWeightLimit() {
        when(droneRepository.findById(drone.getSerialNumber())).thenReturn(Optional.of(drone));

        boolean status = droneService.viewDroneStatus(drone.getSerialNumber());

        assertTrue(status);
    }

    @Test
    void viewDrones_shouldReturnAllDrones() {
        List<Drone> drones = new ArrayList<>();
        drones.add(drone);
        when(droneRepository.findAll()).thenReturn(drones);

        List<Drone> result = droneService.viewDrones();

        assertEquals(1, result.size());
        assertEquals(drone.getSerialNumber(), result.get(0).getSerialNumber());
    }
}
