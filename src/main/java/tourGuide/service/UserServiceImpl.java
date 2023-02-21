package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public void addUserRewards(User user, UserReward userReward) {
        List<UserReward> userRewards = user.getUserRewards();

        if (userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
            userRewards.add(userReward);
        }

        user.setUserRewards(userRewards);
    }
}
