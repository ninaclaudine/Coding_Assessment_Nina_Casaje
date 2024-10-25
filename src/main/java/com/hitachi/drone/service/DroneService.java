package com.hitachi.drone.service;

import com.hitachi.drone.exception.BatterLevelLowException;
import com.hitachi.drone.exception.WeightLimitExceededException;
import com.hitachi.drone.model.Drone;
import com.hitachi.drone.model.Medication;

import java.util.List;
import java.util.Optional;

public interface DroneService {

    public Drone registerDrone(Drone drone);
    Optional<Drone> loadDrone(String id, String medicationId) throws WeightLimitExceededException, BatterLevelLowException;
    public List<Medication> viewDroneLoadMedication(String id);
    public Boolean viewDroneStatus(String id);
    public List<Drone> viewDrones();
    public Drone loadMedication(Drone drone, Medication medication) throws WeightLimitExceededException, BatterLevelLowException;

}
