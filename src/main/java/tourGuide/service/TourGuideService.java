package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.AllCurrentLocations;
import tourGuide.model.NearAttraction;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;
import tripPricer.Provider;

import java.util.List;

public interface TourGuideService {
    List<UserReward> getUserRewards(User user);

    VisitedLocation getUserLocation(User user);

    User getUser(String userName);

    List<User> getAllUsers();

    void addUser(User user);

    List<Provider> getTripDeals(User user);

    VisitedLocation trackUserLocation(User user);

    List<NearAttraction> getNearByAttractions(User user);

    List<AllCurrentLocations> getAllCurrentLocations();

}
