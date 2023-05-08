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

/**
 * Class to link user, rewards and location.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);
	private final GpsUtilServiceImpl gpsUtilServiceImpl;
	private final RewardsServiceImpl rewardsServiceImpl;
	private final TripPricer tripPricer = new TripPricer();

	private final int maxNearestAttraction = 5;
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

	/**
	 * {@inheritDoc}
	 */
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method using trackUserLocationThread even if it is for only one user.
	 */
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
				userService.getLastVisitedLocation(user).get() :
				trackUserLocationThread(Collections.singletonList(user)).get(0);
		return visitedLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(toList());
	}

	/**
	 * {@inheritDoc}
	 */
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method using CompletableFuture to execute trackUserLocation method in multiple thread.
	 */
	public List<VisitedLocation> trackUserLocationThread(List<User> users) {
		List<CompletableFuture<VisitedLocation>> completableFutures = users
				.parallelStream()
				.map(user -> CompletableFuture.supplyAsync(() -> trackUserLocation(user), executorService))
				.collect(toList());

		List<VisitedLocation> visitedLocations = completableFutures.parallelStream().map(CompletableFuture::join).collect(toList());
		return visitedLocations;
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method using calculateRewardsThread even if it is for only one user.
	 */
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtilServiceImpl.getUserLocation(user.getUserId());
		userService.addToVisitedLocations(user, visitedLocation);
		rewardsServiceImpl.calculateRewardsThread(Collections.singletonList(user));
		return visitedLocation;
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method construct a Map of all attraction and the distance between them and one user.
	 * The map is sorted by distance and a specific number of first of the list are adding in a list of NearAttraction object.
	 * The specific number is declared in maxNearestAttraction variable (actually 5).
	 * <br>
	 * trackUserLocationThread's method is using even if it is for only one user.
	 */
	public List<NearAttraction> getNearByAttractions(User user) {
		List<NearAttraction> nearbyAttractions = new ArrayList<>();
		Map<Double, Attraction> attractionMap = new HashMap<>();
		VisitedLocation visitedLocation = trackUserLocationThread(Collections.singletonList(user)).get(0);

		gpsUtilServiceImpl.getAttractions().forEach((attraction) -> {
			attractionMap.put(rewardsServiceImpl.getDistance(attraction, visitedLocation.location), attraction);
		});

		TreeMap<Double, Attraction> sortedAttractionMap = new TreeMap<>(attractionMap);
		sortedAttractionMap.values().stream().limit(maxNearestAttraction).forEach((attraction -> {

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

	/**
	 * {@inheritDoc}
	 */
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
