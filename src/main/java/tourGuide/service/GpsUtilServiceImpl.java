package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Class which rewrite the GpsUtil library due to an error from longitude and latitude format.
 */
@Service
public class GpsUtilServiceImpl implements GpsUtilService {
    private GpsUtil gpsUtil;

    public GpsUtilServiceImpl(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, character ',' is replacing by '.' in Double longitude and latitude.
     */
    public VisitedLocation getUserLocation(UUID userId) {
        this.sleep();
        double longitude = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
        longitude = Double.parseDouble(String.format("%.6f", longitude).replace(',', '.'));
        double latitude = ThreadLocalRandom.current().nextDouble(-85.05112878, 85.05112878);
        latitude = Double.parseDouble(String.format("%.6f", latitude).replace(',', '.'));
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(latitude, longitude), new Date());
        return visitedLocation;
    }

    private void sleep() {
        int random = ThreadLocalRandom.current().nextInt(30, 100);

        try {
            TimeUnit.MILLISECONDS.sleep((long) random);
        } catch (InterruptedException var3) {
        }

    }

    /**
     * {@inheritDoc}
     */
    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }

}

