package tourGuide.model;

import gpsUtil.location.Location;

public class AllCurrentLocations {
    private String userId;
    private Location location;

    public AllCurrentLocations(String userId, Location location) {
        this.userId = userId;
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
