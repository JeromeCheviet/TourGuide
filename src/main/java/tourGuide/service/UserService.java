package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

import java.util.Optional;

public interface UserService {
    void addUserRewards(User user, UserReward userReward);

    void clearVisitedLocations(User user);

    void addToVisitedLocations(User user, VisitedLocation visitedLocation);

    Optional<VisitedLocation> getLastVisitedLocation(User user);
}
