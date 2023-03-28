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
        if (userRewards.stream().filter(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName)).count() == 0) {
            userRewards.add(userReward);
        }

        user.setUserRewards(userRewards);
    }
}
