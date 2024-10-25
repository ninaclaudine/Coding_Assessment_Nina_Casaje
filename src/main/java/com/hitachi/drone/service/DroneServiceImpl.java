package com.hitachi.drone.service;

import com.hitachi.drone.exception.BatterLevelLowException;
import com.hitachi.drone.exception.WeightLimitExceededException;
import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;
import com.hitachi.drone.repository.DroneRepository;
import com.hitachi.drone.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DroneServiceImpl implements DroneService {

    private static final Logger logger = LoggerFactory.getLogger(DroneServiceImpl.class);
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    @Autowired
    public DroneServiceImpl(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }


    @Override
    public Drone registerDrone(Drone drone) {

        if (droneRepository.existsById(drone.getSerialNumber())) {
            logger.error("Drone registration failed: Serial number already exists.");
            throw new IllegalArgumentException("Drone with this serial number already exists.");
        }

        drone.setState(Drone.DroneState.IDLE);
        Drone savedDrone = null;
        try {
            savedDrone = droneRepository.save(drone);
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("Drone registered successfully: {} {}", savedDrone.getSerialNumber(), drone);
        return savedDrone;
    }

    @Override
    public Optional<Drone> loadDrone(String id, String medicationId) throws WeightLimitExceededException, BatterLevelLowException {
        Optional<Medication> medOptional = medicationRepository.findById(Long.valueOf(medicationId));
        Optional<Drone> droneOptional = droneRepository.findById(id);

        if (droneOptional.isPresent() && medOptional.isPresent()) {
            Drone drone = droneOptional.get();

            Medication medication = medOptional.get();

            Drone updatedDrone = loadMedication(drone, medication);
            return Optional.ofNullable(updatedDrone);
        }

        return Optional.empty();
    }

    @Override
    public List<Medication> viewDroneLoadMedication(String id) {
        return droneRepository.findById(id).get().getMedications();
    }

    @Override
    public Boolean viewDroneStatus(String id) {
        Optional<Drone> drone = droneRepository.findById(id);
        if(drone.isPresent()){
            return  drone.get().getTotalMedicationWeight()<= drone.get().getWeightLimit() ? true : false;
        }
        return false;
    }

    @Override
    public List<Drone> viewDrones() {
        return droneRepository.findAll();
    }

    @Override
    public Drone loadMedication(Drone drone, Medication medication) throws WeightLimitExceededException, BatterLevelLowException {
        double newTotalWeight = drone.getTotalMedicationWeight() + medication.getWeight();

        if (newTotalWeight > drone.getWeightLimit()) {
            throw new WeightLimitExceededException("Total weight exceeds the drone's weight limit.");
        }

        if ((drone.getState().equals(Drone.DroneState.IDLE) || drone.getState().equals(Drone.DroneState.LOADING))
                && (drone.getTotalMedicationWeight() + medication.getWeight() <= drone.getWeightLimit())) {
            Medication medicationToAdd = new Medication();
            medicationToAdd.setCode(medication.getCode());
            medicationToAdd.setImage(medication.getImage());
            medicationToAdd.setName(medication.getName());
            medicationToAdd.setQuantity(medication.getQuantity()+1);
            medicationToAdd.setWeight(medication.getWeight() * medicationToAdd.getQuantity());
            medicationToAdd.setDrone(drone);

            drone.getMedications().add(medicationToAdd);

            if (drone.getState().equals(Drone.DroneState.IDLE)) {
                if(drone.getBatteryCapacity() <= 25){
                    throw new BatterLevelLowException("Battery is below 25% cannot enter LOADING State. ");
                }
                drone.setState(Drone.DroneState.LOADING);
            }

            droneRepository.save(drone);
            return drone;
        }
        return null;
    }
}
