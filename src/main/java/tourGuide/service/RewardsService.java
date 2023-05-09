package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import tourGuide.model.user.User;

import java.util.List;

/**
 * Interface link to Rewards
 */
public interface RewardsService {
    /**
     * Modify the Proximity Buffer variable.
     *
     * @param proximityBuffer New integer to set in proximityBuffer.
     */
    void setProximityBuffer(int proximityBuffer);

    /**
     * Reset the Proximity Buffer variable with default data.
     */
    void setDefaultProximityBuffer();

    /**
     * Calculate rewards for one user.
     *
     * @param user
     */
    void calculateRewards(User user);

    /**
     * Calculate rewards for a list of user.
     *
     * @param users
     */
    void calculateRewardsThread(List<User> users);

    /**
     * Calculate the difference between an attraction and a location.
     *
     * @param attraction Object
     * @param location   Object
     * @return true if attraction and location are in the same area, else false.
     */
    boolean isWithinAttractionProximity(Attraction attraction, Location location);

    /**
     * Get the reward's points of one attraction to an user.
     *
     * @param attraction Object
     * @param user       Object
     * @return The number of Reward's points.
     */
    int getRewardPoints(Attraction attraction, User user);

    /**
     * Get distance between two locations.
     *
     * @param loc1 Object
     * @param loc2 Object
     * @return the distance in Double format.
     */
    double getDistance(Location loc1, Location loc2);

}
