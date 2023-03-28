package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(RewardsService.class);

    @Override
    public void addUserRewards(User user, UserReward userReward) {
        List<UserReward> userRewards = user.getUserRewards();
        if (userRewards.stream().filter(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName)).count() == 0) {
            userRewards.add(userReward);
        }

        user.setUserRewards(userRewards);
    }

    @Override
    public void clearVisitedLocations(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        if (!visitedLocations.isEmpty()) {
            visitedLocations.clear();
        }
    }

    @Override
    public void addToVisitedLocations(User user, VisitedLocation visitedLocation) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        user.setVisitedLocations(visitedLocations);
    }
}
