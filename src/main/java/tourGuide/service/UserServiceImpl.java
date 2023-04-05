package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

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
        user.setVisitedLocations(visitedLocations);
    }

    @Override
    public void addToVisitedLocations(User user, VisitedLocation visitedLocation) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        user.setVisitedLocations(visitedLocations);
    }

    @Override
    public Optional<VisitedLocation> getLastVisitedLocation(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        VisitedLocation visitedLocation = null;

        if (!visitedLocations.isEmpty()) {
            visitedLocation = visitedLocations.get(visitedLocations.size() - 1);
        }

        return Optional.ofNullable(visitedLocation);
    }
}
