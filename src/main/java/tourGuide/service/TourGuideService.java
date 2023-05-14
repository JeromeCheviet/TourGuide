package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.AllCurrentLocations;
import tourGuide.model.NearAttraction;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;
import tripPricer.Provider;

import java.util.List;

/**
 * Interface link to TourGuide
 */
public interface TourGuideService {
    /**
     * Get all rewords of one user
     * @param user Object
     * @return List of UserReward object.
     */
    List<UserReward> getUserRewards(User user);

    /**
     * Get user location
     * @param user Object
     * @return VisitedLocation object.
     */
    VisitedLocation getUserLocation(User user);

    /**
     * Get information from a user
     * @param userName String
     * @return User object
     */
    User getUser(String userName);

    Boolean isUserExist(String userName);

    /**
     * Get list of all users.
     *
     * @return List of user object
     */
    List<User> getAllUsers();

    /**
     * Add a user
     *
     * @param user Object
     */
    void addUser(User user);

    /**
     *  Get a list of provider use by one user.
     * @param user Object
     * @return List of provider object
     */
    List<Provider> getTripDeals(User user);

    /**
     *  Get the actual location for a list of User.
     *
     * @param users List of User Object
     * @return List of VisitedLocation Object
     */
    List<VisitedLocation> trackUserLocationThread(List<User> users);

    /**
     * Get the actual user location.
     *
     * @param user object
     * @return VisitedLocation object
     */
    VisitedLocation trackUserLocation(User user);

    /**
     * Get attractions near of one user
     *
     * @param user object
     * @return List of NearAttraction object
     */
    List<NearAttraction> getNearByAttractions(User user);

    /**
     * List of actual location for all users.
     *
     * @return List of AllCurrentLocations object.
     */
    List<AllCurrentLocations> getAllCurrentLocations();

    void linkUpdatePreferenceToAnExistingUser(String userName, UpdateUserPreferences updateUserPreferences);
}
