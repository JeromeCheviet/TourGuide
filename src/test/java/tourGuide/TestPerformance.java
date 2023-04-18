package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.*;
import tourGuide.model.user.User;

public class TestPerformance {
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	
	//@Ignore
	@Test
	public void highVolumeTrackLocation() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideServiceImpl.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		/*for(User user : allUsers) {
			tourGuideServiceImpl.trackUserLocation(user);
			//tourGuideServiceImpl.trackUserLocationThread(Collections.singletonList(user));
		}*/
		tourGuideServiceImpl.trackUserLocationThread(allUsers);
		stopWatch.stop();
		tourGuideServiceImpl.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	
	//@Ignore
	@Test
	public void highVolumeGetRewards() {
		GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
		RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
		UserService userService = new UserServiceImpl();

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);
		
	    Attraction attraction = gpsUtilServiceImpl.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideServiceImpl.getAllUsers();
		allUsers.forEach(u -> userService.addToVisitedLocations(u, new VisitedLocation(u.getUserId(), attraction, new Date())));
	    //allUsers.forEach(u -> rewardsServiceImpl.calculateRewards(u));
		rewardsServiceImpl.calculateRewardsThread(allUsers);
	    
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideServiceImpl.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
