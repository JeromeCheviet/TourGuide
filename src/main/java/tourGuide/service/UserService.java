package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

import java.util.Optional;

/**
 * Interface link to user.
 */
public interface UserService {
    /**
     * Add rewards for a specific user
     *
     * @param user object
     * @param userReward object
     */
    void addUserRewards(User user, UserReward userReward);

    /**
     * Delete all visited location for a specific user
     * @param user object
     */
    void clearVisitedLocations(User user);

    /**
     * Add a visited location for a specific user.
     * @param user object
     * @param visitedLocation object
     */
    void addToVisitedLocations(User user, VisitedLocation visitedLocation);

    /**
     * Get the last visited location for a specific user.
     * @param user object
     * @return VisitedLocation object if existing.
     */
    Optional<VisitedLocation> getLastVisitedLocation(User user);
}
