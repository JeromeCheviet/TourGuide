package tourGuide.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotExistException;
import tourGuide.model.AllCurrentLocations;
import tourGuide.model.NearAttraction;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.model.user.UserPreferences;
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
	private InternalUserService internalUserService = new InternalUserService();

	private final int maxNearestAttraction = 5;
	public final Tracker tracker;

	private final ExecutorService executorService = Executors.newFixedThreadPool(85);

	private UserService userService = new UserServiceImpl();

	boolean testMode = true;

	public TourGuideServiceImpl(GpsUtilServiceImpl gpsUtilServiceImpl, RewardsServiceImpl rewardsServiceImpl) {
		this.gpsUtilServiceImpl = gpsUtilServiceImpl;
		this.rewardsServiceImpl = rewardsServiceImpl;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalUserService.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method using trackUserLocationThread even if it is for only one user.
	 */
	@Override
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
				userService.getLastVisitedLocation(user).get() :
				trackUserLocationThread(Collections.singletonList(user)).get(0);
		return visitedLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUser(String userName) {
		return internalUserService.internalUserMap.get(userName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isUserExist(String userName) {
		return internalUserService.internalUserMap.containsKey(userName) ? true : false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getAllUsers() {
		return internalUserService.internalUserMap.values().stream().collect(toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUser(User user) {
		if(!internalUserService.internalUserMap.containsKey(user.getUserName())) {
			internalUserService.internalUserMap.put(user.getUserName(), user);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(internalUserService.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method using CompletableFuture to execute trackUserLocation method in multiple thread.
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
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

	/**
	 * {@inheritDoc}
	 *
	 * if the user exists, it is sent to the class userService with the updated preferences to be applying.
	 * Else, a personal exception is throwing.
	 */
	@Override
	public void linkUpdatePreferenceToAnExistingUser(String userName, UpdateUserPreferences updateUserPreferences) {
		if (isUserExist(userName)) {
			User user = getUser(userName);
			userService.updatePreferences(user, updateUserPreferences);
		} else {
			throw new UserNotExistException(userName);
		}
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}
}
