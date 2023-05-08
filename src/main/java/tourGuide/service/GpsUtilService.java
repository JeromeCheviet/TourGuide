package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.UUID;

/**
 * Interface link to GpsUtil operations.
 */
public interface GpsUtilService {
    /**
     * Get visited location from a specific user.
     *
     * @param userId
     * @return VisitedLocation's object.
     */
    VisitedLocation getUserLocation(UUID userId);

    /**
     * Get a list of all attractions.
     *
     * @return List of Attraction's object.
     */
    List<Attraction> getAttractions();
}
