package tourGuide.service;

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
        logger.debug("liste : " + String.valueOf(userRewards.size()));
        logger.debug("count : " + String.valueOf(userRewards.stream().filter(r -> r.attraction.attractionName.equals(userReward.attraction)).count()));
        if (userRewards.stream().filter(r -> r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
            userRewards.add(userReward);
            logger.debug("added : " + userReward.attraction.attractionName);
        }

        user.setUserRewards(userRewards);
    }
}
