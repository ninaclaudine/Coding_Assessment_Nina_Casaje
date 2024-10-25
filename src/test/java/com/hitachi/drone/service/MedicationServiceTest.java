package com.hitachi.drone.service;

import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;
import com.hitachi.drone.repository.DroneRepository;
import com.hitachi.drone.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MedicationServiceTest {

    @InjectMocks
    private MedicationService medicationService;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private DroneRepository droneRepository;

    private Drone testDrone;
    private Medication medication1;
    private Medication medication2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test drone and medications
        testDrone = new Drone();
        testDrone.setSerialNumber("001");
        testDrone.setModel(Drone.DroneModel.CRUISERWEIGHT);
        testDrone.setBatteryCapacity(25);
        testDrone.setWeightLimit(1000);
        testDrone.setState(Drone.DroneState.IDLE);

        medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("Biogesic");
        medication1.setWeight(100);
        medication1.setCode("MED01");
        medication1.setImage("images/pain_relief.png");
        medication1.setQuantity(1);
        medication1.setDrone(testDrone);

        medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Alaxan");
        medication2.setWeight(150);
        medication2.setCode("MED02");
        medication2.setImage("images/antibiotic.png");
        medication2.setQuantity(1);
        medication2.setDrone(testDrone);
    }

    @Test
    void init_shouldPreloadDroneAndMedications() {
        // Act
        medicationService.init();

        // Verify that the drone was saved
        verify(droneRepository, times(1)).save(testDrone);

        // Verify that the medications were saved
        verify(medicationRepository, times(1)).save(medication1);
        verify(medicationRepository, times(1)).save(medication2);
    }

    @Test
    void init_shouldCreateCorrectDrone() {
        // Act
        medicationService.init();

        // Verify that the drone is created with expected values
        verify(droneRepository).save(testDrone);
        assertEquals("001", testDrone.getSerialNumber());
        assertEquals(Drone.DroneModel.CRUISERWEIGHT, testDrone.getModel());
        assertEquals(25, testDrone.getBatteryCapacity());
        assertEquals(1000, testDrone.getWeightLimit());
        assertEquals(Drone.DroneState.IDLE, testDrone.getState());
    }

    @Test
    void init_shouldCreateCorrectMedications() {
        // Act
        medicationService.init();

        // Verify that the medications are created with expected values
        verify(medicationRepository).save(medication1);
        verify(medicationRepository).save(medication2);

        assertEquals("Biogesic", medication1.getName());
        assertEquals(100, medication1.getWeight());
        assertEquals("MED01", medication1.getCode());
        assertEquals("images/pain_relief.png", medication1.getImage());

        assertEquals("Alaxan", medication2.getName());
        assertEquals(150, medication2.getWeight());
        assertEquals("MED02", medication2.getCode());
        assertEquals("images/antibiotic.png", medication2.getImage());
    }
}
