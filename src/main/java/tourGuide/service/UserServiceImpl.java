package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.model.user.User;
import tourGuide.model.user.UserPreferences;
import tourGuide.model.user.UserReward;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserRewards(User user, UserReward userReward) {
        List<UserReward> userRewards = user.getUserRewards();
        if (userRewards.stream().filter(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName)).count() == 0) {
            userRewards.add(userReward);
        }

        user.setUserRewards(userRewards);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearVisitedLocations(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        if (!visitedLocations.isEmpty()) {
            visitedLocations.clear();
        }
        user.setVisitedLocations(visitedLocations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToVisitedLocations(User user, VisitedLocation visitedLocation) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        user.setVisitedLocations(visitedLocations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<VisitedLocation> getLastVisitedLocation(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        VisitedLocation visitedLocation = null;

        if (!visitedLocations.isEmpty()) {
            visitedLocation = visitedLocations.get(visitedLocations.size() - 1);
        }

        return Optional.ofNullable(visitedLocation);
    }

    @Override
    public void updatePreferences(User user, UpdateUserPreferences updateUserPreferences) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(updateUserPreferences.getAttractionProximity());
        userPreferences.setLowerPricePoint(Money.of(updateUserPreferences.getLowerPricePoint(), updateUserPreferences.getCurrency()));
        userPreferences.setHighPricePoint(Money.of(updateUserPreferences.getHighPricePoint(), updateUserPreferences.getCurrency()));
        userPreferences.setTripDuration(updateUserPreferences.getTripDuration());
        userPreferences.setTicketQuantity(updateUserPreferences.getTicketQuantity());
        userPreferences.setNumberOfAdults(updateUserPreferences.getNumberOfAdults());
        userPreferences.setNumberOfChildren(updateUserPreferences.getNumberOfChildren());

        user.setUserPreferences(userPreferences);
    }

}
