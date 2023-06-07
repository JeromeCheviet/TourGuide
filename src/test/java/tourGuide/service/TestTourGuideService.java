package tourGuide.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import tourGuide.model.AllCurrentLocations;
import tourGuide.model.NearAttraction;
import tourGuide.service.*;
import tourGuide.model.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Test
	public void trackUserLocation() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideServiceImpl.trackUserLocationThread(Collections.singletonList(user)).get(0);
		tourGuideServiceImpl.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void getUserLocation() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		VisitedLocation visitedLocation = tourGuideServiceImpl.getUserLocation(user);

		tourGuideServiceImpl.tracker.stopTracking();

		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void getUserLocation_WithLastVisitedLocation() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		UserService userService = new UserServiceImpl();

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		//tourGuideServiceImpl.addUser(user);

		Attraction attraction = gpsUtilServiceImpl.getAttractions().get(0);

		userService.addToVisitedLocations(user, new VisitedLocation(user.getUserId(), attraction, new Date()));

		VisitedLocation visitedLocation = tourGuideServiceImpl.getUserLocation(user);

		tourGuideServiceImpl.tracker.stopTracking();

		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideServiceImpl.addUser(user);
		tourGuideServiceImpl.addUser(user2);
		
		User retrivedUser = tourGuideServiceImpl.getUser(user.getUserName());
		User retrivedUser2 = tourGuideServiceImpl.getUser(user2.getUserName());

		tourGuideServiceImpl.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(2);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideServiceImpl.addUser(user);
		tourGuideServiceImpl.addUser(user2);
		
		List<User> allUsers = tourGuideServiceImpl.getAllUsers();

		tourGuideServiceImpl.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideServiceImpl.trackUserLocationThread(Collections.singletonList(user)).get(0);
		
		tourGuideServiceImpl.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
		List<NearAttraction> attractions = tourGuideServiceImpl.getNearByAttractions(user);
		
		tourGuideServiceImpl.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideServiceImpl.getTripDeals(user);
		
		tourGuideServiceImpl.tracker.stopTracking();
		
		assertEquals(5, providers.size());
	}

	@Test
	public void getAllCurrentLocations() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		UserService userService = new UserServiceImpl();

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideServiceImpl.addUser(user);
		tourGuideServiceImpl.addUser(user2);

		Attraction attraction = gpsUtilServiceImpl.getAttractions().get(0);
		Attraction attraction2 = gpsUtilServiceImpl.getAttractions().get(1);

		userService.addToVisitedLocations(user, new VisitedLocation(user.getUserId(), attraction, new Date()));
		userService.addToVisitedLocations(user, new VisitedLocation(user.getUserId(), attraction2, new Date()));
		userService.addToVisitedLocations(user2, new VisitedLocation(user.getUserId(), attraction, new Date()));

		List<AllCurrentLocations> actualAllCurrentLocations = tourGuideServiceImpl.getAllCurrentLocations();

		tourGuideServiceImpl.tracker.stopTracking();

		assertEquals(2, actualAllCurrentLocations.size());
		assertEquals(attraction2.latitude, actualAllCurrentLocations.get(0).getLocation().latitude, 0);
	}
}
