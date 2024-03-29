package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserServiceTest {
    private UserService userService = new UserServiceImpl();
    private User expectedUser;
    private UserReward expectedUserReward;
    private VisitedLocation expectedVisitedLocation;
    private Attraction expectedAttraction;
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
        expectedAttraction = new Attraction("Futuroscope", "Poitiers", "Vienne", 1.23346, 3.76442);
        expectedUserReward = new UserReward(expectedVisitedLocation, expectedAttraction);

    }

    @Test
    public void testAddUserRewards() {
        List<UserReward> expectedUserRewardList = new ArrayList<>();
        expectedUserRewardList.add(expectedUserReward);

        userService.addUserRewards(expectedUser, expectedUserReward);

        List<UserReward> actualUserRewardList = expectedUser.getUserRewards();

        assertEquals(expectedUserRewardList, actualUserRewardList);

    }

    @Test
    public void testAddUserReward_whenMultipleRewards() {
        Attraction secondeAttraction = new Attraction("Center Parc", "Morton", "Vienne", 1.23356, 3.76652);
        UserReward secondUserReward = new UserReward(expectedVisitedLocation, secondeAttraction);

        List<UserReward> expectedUserRewardList = new ArrayList<>();
        expectedUserRewardList.add(expectedUserReward);
        expectedUserRewardList.add(secondUserReward);

        expectedUserRewardList.stream().forEach( ur -> userService.addUserRewards(expectedUser, ur));

        List<UserReward> actualUserRewardList = expectedUser.getUserRewards();

        assertEquals(expectedUserRewardList.size(), actualUserRewardList.size());

    }

    @Test
    public void testAddUserReward_whenSameReward() {
        List<UserReward> expectedUserRewardList = new ArrayList<>();
        expectedUserRewardList.add(expectedUserReward);

        List<UserReward> userRewards = new ArrayList<>();
        userRewards.add(expectedUserReward);
        userRewards.add(expectedUserReward);

        userRewards.stream().forEach( ur -> userService.addUserRewards(expectedUser, ur));

        List<UserReward> actualUserRewardList = expectedUser.getUserRewards();

        assertEquals(expectedUserRewardList.size(), actualUserRewardList.size());

    }

    @Test
    public void testAddToVisitedLocation() {
        userService.addToVisitedLocations(expectedUser, expectedVisitedLocation);
        List<VisitedLocation> actualVisitedLocation = expectedUser.getVisitedLocations();

        assertEquals(expectedVisitedLocation.location, actualVisitedLocation.get(0).location);
    }

    @Test
    public void testClearVisitedLocation() {
        userService.addToVisitedLocations(expectedUser, expectedVisitedLocation);
        userService.clearVisitedLocations(expectedUser);

        assertEquals(0, expectedUser.getVisitedLocations().size());
    }

    @Test
    public void testGetLastVisitedLocation() {
        VisitedLocation actualLastVisitedLocation = null;
        Location secondeLocation = new Location(1.23356, 3.76652);
        VisitedLocation secondeVisitedLocation = new VisitedLocation(UUID.randomUUID(), secondeLocation, expectedDate);
        List<VisitedLocation> visitedLocations = new ArrayList<>();
        visitedLocations.add(expectedVisitedLocation);
        visitedLocations.add(secondeVisitedLocation);
        expectedUser.setVisitedLocations(visitedLocations);

        Optional<VisitedLocation> visitedLocation = userService.getLastVisitedLocation(expectedUser);

        if (visitedLocation.isPresent()) {
            actualLastVisitedLocation = visitedLocation.get();
        }
        assertEquals(secondeVisitedLocation.userId, actualLastVisitedLocation.userId);
    }

    @Test
    public void testGetLastVisitedLocation_whenListEmpty_returnNull() {
        VisitedLocation actualLastVisitedLocation = null;
        userService.clearVisitedLocations(expectedUser);

        Optional<VisitedLocation> visitedLocation = userService.getLastVisitedLocation(expectedUser);

        if (visitedLocation.isPresent()) {
            actualLastVisitedLocation = visitedLocation.get();
        }

        assertNull(actualLastVisitedLocation);
    }

    @Test
    public void testUpdateUserPreferences() {
        int actualAttractionProximity = 8;
        String actualCurrency = "EUR";
        int actualLowerPricePoint = 0;
        int actualHighPricePoint = 10;
        int actualTripDuration = 3;
        int actualTicketQuantity = 4;
        int actualNumberOfAdults = 2;
        int actualNumberOfChildren = 2;

        UpdateUserPreferences updateUserPreferences = new UpdateUserPreferences(actualAttractionProximity, actualCurrency,
                actualLowerPricePoint, actualHighPricePoint, actualTripDuration, actualTicketQuantity, actualNumberOfAdults, actualNumberOfChildren);

        userService.updatePreferences(expectedUser, updateUserPreferences);

        assertEquals(expectedUser.getUserPreferences().getAttractionProximity(), actualAttractionProximity);
        assertEquals(expectedUser.getUserPreferences().getLowerPricePoint(), Money.of(actualLowerPricePoint, actualCurrency));
        assertEquals(expectedUser.getUserPreferences().getHighPricePoint(), Money.of(actualHighPricePoint, actualCurrency));
        assertEquals(expectedUser.getUserPreferences().getTripDuration(), actualTripDuration);
        assertEquals(expectedUser.getUserPreferences().getTicketQuantity(), actualTicketQuantity);
        assertEquals(expectedUser.getUserPreferences().getNumberOfAdults(), actualNumberOfAdults);
        assertEquals(expectedUser.getUserPreferences().getNumberOfChildren(), actualNumberOfChildren);
    }
}
