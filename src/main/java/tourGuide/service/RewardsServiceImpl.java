package tourGuide.service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

@Service
public class RewardsServiceImpl implements RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtilServiceImpl gpsUtilServiceImpl;
	private final RewardCentral rewardsCentral;
	private final ExecutorService executorService = Executors.newFixedThreadPool(500);

	private UserService userService = new UserServiceImpl();
	
	public RewardsServiceImpl(GpsUtilServiceImpl gpsUtilServiceImpl, RewardCentral rewardCentral) {
		this.gpsUtilServiceImpl = gpsUtilServiceImpl;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewardsThread(List<User> users) {
		List<CompletableFuture<Void>> completableFutures = users
				.stream()
				.map(user -> CompletableFuture.runAsync(() -> calculateRewards(user), executorService))
				.collect(Collectors.toList());

		completableFutures.forEach(CompletableFuture::join);
		//executorService.shutdown();
	}

	public void calculateRewards(User user) {
		CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
		CopyOnWriteArrayList<Attraction> attractions = new CopyOnWriteArrayList<>(gpsUtilServiceImpl.getAttractions());

		for(VisitedLocation visitedLocation : userLocations) {
			attractions.forEach(attraction -> addRewardToUser(visitedLocation, attraction, user));
		}

	}

	private void addRewardToUser(VisitedLocation visitedLocation, Attraction attraction, User user) {
		if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
			if(nearAttraction(visitedLocation, attraction)) {
				userService.addUserRewards(user,
						new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
			}
		}
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
