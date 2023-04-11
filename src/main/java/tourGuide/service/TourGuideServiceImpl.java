package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.AllCurrentLocations;
import tourGuide.model.NearAttraction;
import tourGuide.tracker.Tracker;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static java.util.stream.Collectors.toList;

@Service
public class TourGuideServiceImpl implements TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	//private final GpsUtil gpsUtil;
	private final GpsUtilServiceImpl gpsUtilServiceImpl;
	private final RewardsServiceImpl rewardsServiceImpl;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;

	private final ExecutorService executorService = Executors.newFixedThreadPool(100);

	private UserService userService = new UserServiceImpl();

	boolean testMode = true;

	public TourGuideServiceImpl(GpsUtilServiceImpl gpsUtilServiceImpl, RewardsServiceImpl rewardsServiceImpl) {
		this.gpsUtilServiceImpl = gpsUtilServiceImpl;
		this.rewardsServiceImpl = rewardsServiceImpl;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
				user.getLastVisitedLocation() :
				trackUserLocation(user);
		return visitedLocation;
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(toList());
	}

	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtilServiceImpl.getUserLocation(user.getUserId());
		userService.addToVisitedLocations(user, visitedLocation);
		//rewardsServiceImpl.calculateRewards(user);
		rewardsServiceImpl.calculateRewardsThread(Collections.singletonList(user));
		return visitedLocation;
	}

	public List<VisitedLocation> trackUserLocationThread(List<User> users) {
		List<CompletableFuture<VisitedLocation>> completableFutures = users
				.parallelStream()
				.map(user -> CompletableFuture.supplyAsync(() -> trackUserLocation(user), executorService))
				.collect(toList());

		List<VisitedLocation> visitedLocations = completableFutures.parallelStream().map(CompletableFuture::join).collect(toList());
		//visitedLocations.stream().forEach(visitedLocation -> logger.debug(visitedLocation.userId.toString()));
		//return completableFutures.parallelStream().map(CompletableFuture::join).collect(toList());
		//executorService.shutdown();
		return visitedLocations;
	}

	public List<NearAttraction> getNearByAttractions(User user) {
		List<NearAttraction> nearbyAttractions = new ArrayList<>();
		Map<Double, Attraction> attractionMap = new HashMap<>();
		VisitedLocation visitedLocation = trackUserLocation(user);

		gpsUtilServiceImpl.getAttractions().forEach((attraction) -> {
			attractionMap.put(rewardsServiceImpl.getDistance(attraction, visitedLocation.location), attraction);
		});

		TreeMap<Double, Attraction> sortedAttractionMap = new TreeMap<>(attractionMap);
		sortedAttractionMap.values().stream().limit(5).forEach((attraction -> {

			nearbyAttractions.add(new NearAttraction(attraction.attractionName,
					attraction.latitude,
					attraction.longitude,
					visitedLocation.location.latitude,
					visitedLocation.location.longitude,
					rewardsServiceImpl.getDistance(attraction, visitedLocation.location),
					rewardsServiceImpl.getRewardPoints(attraction, user)
			));
		}));

		return nearbyAttractions;
	}

	public List<AllCurrentLocations> getAllCurrentLocations() {
		List<User> users = getAllUsers();
		List<AllCurrentLocations> allCurrentLocations = new ArrayList<>();

		users.forEach(user -> {
			Optional<VisitedLocation> visitedLocation = userService.getLastVisitedLocation(user);
			visitedLocation.ifPresent(location ->
					allCurrentLocations.add(new AllCurrentLocations(user.getUserId().toString(), location.location)));
		});

        return allCurrentLocations;
    }

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

	/**********************************************************************************
	 *
	 * Methods Below: For Internal Testing
	 *
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			logger.debug("userId : " + user.getUserId());
			userService.addToVisitedLocations(user, new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
