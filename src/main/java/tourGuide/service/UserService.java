package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public interface UserService {
    void addUserRewards(User user, UserReward userReward);

    void clearVisitedLocations(User user);

    void addToVisitedLocations(User user, VisitedLocation visitedLocation);
}
