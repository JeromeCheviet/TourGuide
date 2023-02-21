package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Test;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

//@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    //@InjectMocks
    private UserService userService = new UserServiceImpl();

    //@Mock
    private User expectedUser;

    //@Mock
    private UserReward expectedUserReward;

    //@Mock
    private VisitedLocation expectedVisitedLocation;

    //@Mock
    private Attraction expectedAttraction;

    //@Mock
    private Location expectedLocation;

    private Date expectedDate = new Date(2022, 12, 25, 8, 30, 25);

    @Before
    public void setUp() {
        UUID expectedUUID = UUID.randomUUID();
        String expectedUserName = "john";
        String expectedPhoneNumber = "000";
        String expectedEmailAddress = "john@tourguide.com";
        expectedUser = new User(expectedUUID, expectedUserName, expectedPhoneNumber, expectedEmailAddress);

        expectedLocation = new Location(1.23345, 3.76543);
        expectedVisitedLocation = new VisitedLocation(expectedUUID, expectedLocation, expectedDate);
        expectedAttraction = new Attraction("Fururoscope", "Poitiers", "Vienne", 1.23346, 3.76442);
        expectedUserReward = new UserReward(expectedVisitedLocation, expectedAttraction);

    }

    @Test
    public void TestAddUserRewards() {
        List<UserReward> expectedUserRewardList = new ArrayList<>();
        expectedUserRewardList.add(expectedUserReward);

        userService.addUserRewards(expectedUser, expectedUserReward);

        List<UserReward> actualUserRewardList = expectedUser.getUserRewards();

        assertEquals(expectedUserRewardList, actualUserRewardList);

    }


}
