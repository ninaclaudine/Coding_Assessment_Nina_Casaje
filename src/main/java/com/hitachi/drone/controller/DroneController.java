package com.hitachi.drone.controller;

import com.hitachi.drone.exception.BatterLevelLowException;
import com.hitachi.drone.exception.WeightLimitExceededException;
import com.hitachi.drone.model.Drone;
import com.hitachi.drone.service.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/drone")
public class DroneController {

    @Autowired
    private DroneService droneService;

    @PostMapping("/register")
    public ResponseEntity<Drone> registerDrone(@RequestBody Drone drone){
        return ResponseEntity.ok(droneService.registerDrone(drone));
    }

    @PostMapping("/{droneId}/{medicationId}/load")
    public ResponseEntity<?> loadDrone(@PathVariable String droneId,
                                           @PathVariable String medicationId) {
        try {
            Optional<Drone> updatedDrone = droneService.loadDrone(droneId, medicationId);
            return ResponseEntity.ok(updatedDrone.get());
        } catch (WeightLimitExceededException e) {
            return ResponseEntity.ok(Collections.singletonMap("message",e.getMessage()));
        } catch (BatterLevelLowException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{id}/load")
    public ResponseEntity<?> viewDroneLoad(@PathVariable String id){
        return ResponseEntity.ok(droneService.viewDroneLoadMedication(id));
    }

    @GetMapping("{id}/status")
    public ResponseEntity<?> viewDroneStats(@PathVariable String id){
        return ResponseEntity.ok(Collections.singletonMap("is_available_for_loading", droneService.viewDroneStatus(id)));
       
    }

    @GetMapping("/view")
    public ResponseEntity<List<Drone>> viewDrone(){
        return ResponseEntity.ok(droneService.viewDrones());
    }

}
