package tourGuide.service;

import tourGuide.user.User;
import tourGuide.user.UserReward;

public interface UserService {
    void addUserRewards(User user, UserReward userReward);
}
