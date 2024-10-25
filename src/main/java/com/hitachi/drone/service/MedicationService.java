package com.hitachi.drone.service;

import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;
import com.hitachi.drone.repository.DroneRepository;
import com.hitachi.drone.repository.MedicationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final DroneRepository droneRepository;

    @Autowired
    public MedicationService(MedicationRepository medicationRepository, DroneRepository droneRepository) {
        this.medicationRepository = medicationRepository;
        this.droneRepository = droneRepository;
    }

    /*
    * NOTE
    * Pre-loaded values only.
    * Assumed that there are only fixed medicines available
    * Use Case : Cannot just add medicine, use pre-loaded values*/
    @PostConstruct
    public void init() {
        Drone drone = new Drone();
        drone.setSerialNumber("001");
        drone.setModel(Drone.DroneModel.CRUISERWEIGHT);
        drone.setBatteryCapacity(25);
        drone.setWeightLimit(1000);
        drone.setState(Drone.DroneState.IDLE);

        droneRepository.save(drone);

        Medication medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("Biogesic");
        medication1.setWeight(100);
        medication1.setCode("MED01");
        medication1.setImage("images/pain_relief.png");
        medication1.setQuantity(1);
        medication1.setDrone(drone);

        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Alaxan");
        medication2.setWeight(150);
        medication2.setCode("MED02");
        medication2.setImage("images/antibiotic.png");
        medication2.setQuantity(1);
        medication2.setDrone(drone);

        medicationRepository.save(medication1);
        medicationRepository.save(medication2);
    }

}
