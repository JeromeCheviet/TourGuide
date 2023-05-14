package tourGuide.service;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

public class TestRewardsService {

	@Test
	public void userGetRewards() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		UserService userService = new UserServiceImpl();

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtilServiceImpl.getAttractions().get(0);
		userService.addToVisitedLocations(user, new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideServiceImpl.trackUserLocationThread(Collections.singletonList(user)).get(0);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideServiceImpl.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		Attraction attraction = gpsUtilServiceImpl.getAttractions().get(0);
		assertTrue(rewardsServiceImpl.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		rewardsServiceImpl.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		rewardsServiceImpl.calculateRewardsThread(Collections.singletonList(tourGuideServiceImpl.getAllUsers().get(0)));
		List<UserReward> userRewards = tourGuideServiceImpl.getUserRewards(tourGuideServiceImpl.getAllUsers().get(0));
		tourGuideServiceImpl.tracker.stopTracking();

		assertEquals(gpsUtilServiceImpl.getAttractions().size(), userRewards.size());
	}

}
