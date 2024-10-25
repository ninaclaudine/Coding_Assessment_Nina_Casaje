package com.hitachi.drone.service;

import com.hitachi.drone.model.Drone;
import com.hitachi.drone.repository.DroneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class DroneStateScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DroneStateScheduler.class);

    private final DroneRepository droneRepository;
    private final DroneServiceImpl droneService;

    public DroneStateScheduler(DroneRepository droneRepository, DroneServiceImpl droneService) {
        this.droneRepository = droneRepository;
        this.droneService = droneService;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void updateDroneStates() {
        List<Drone> drones = droneRepository.findAll();
        for (Drone drone : drones) {
            if (drone.getState() == null) {
                logger.warn("Drone {} has a null state. Skipping.", drone.getSerialNumber());
                continue;
            }

            switch (drone.getState()) {
                case IDLE:
                    logger.info("Drone {} is IDLE", drone.getSerialNumber());
                    break;
                case LOADING:
                    drone.setState(Drone.DroneState.LOADED);
                    logger.info("Drone {} is LOADING and now changed to LOADED", drone.getSerialNumber());
                    break;
                case LOADED:
                    drone.setState(Drone.DroneState.DELIVERING);
                    logger.info("Drone {} is LOADED and now changing to DELIVERING", drone.getSerialNumber());
                    break;
                case DELIVERING:
                    drone.setState(Drone.DroneState.DELIVERED);
                    logger.info("Drone {} has DELIVERED the package", drone.getSerialNumber());
                    // Reduce battery after delivery
                    drone.setBatteryCapacity(drone.getBatteryCapacity() - 10);
                    break;
                case DELIVERED:
                    drone.setState(Drone.DroneState.RETURNING);
                    logger.info("Drone {} is RETURNING after delivery", drone.getSerialNumber());
                    break;
                case RETURNING:
                    drone.setState(Drone.DroneState.IDLE);
                    logger.info("Drone {} has returned and is now IDLE", drone.getSerialNumber());
                    break;
                default:
                    logger.warn("Drone {} is in an unknown state: {}", drone.getSerialNumber(), drone.getState());
                    break;
            }
            droneRepository.save(drone);
        }
    }
}
