package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.user.User;
import tourGuide.service.TourGuideServiceImpl;
import tripPricer.Provider;

import java.util.List;

/**
 * Class which manage REST API Controller
 */
@RestController
public class TourGuideController {

    @Autowired
    TourGuideServiceImpl tourGuideServiceImpl;

    /**
     * Method to loading the root page
     *
     * @return String A welcome message.
     */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Method to get user location.
     *
     * @param userName - Name of user
     * @return Object location in Json format.
     */
    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideServiceImpl.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    /**
     * Method to get the closest five tourist attractions to the user.
     *
     * @param userName - Name of user
     * @return Object NearAttraction in Json format
     */
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        User user = getUser(userName);
        return JsonStream.serialize(tourGuideServiceImpl.getNearByAttractions(user));
    }

    /**
     * Method to get the rewords for one user.
     *
     * @param userName - Name of user
     * @return A list of Rewards objects in Json format.
     */
    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideServiceImpl.getUserRewards(getUser(userName)));
    }

    /**
     * Method to get a list of every user's most recent location
     *
     * @return A list of AllCurrentLocations objects in Json format.
     */
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        return JsonStream.serialize(tourGuideServiceImpl.getAllCurrentLocations());
    }

    /**
     * Method to get a list of provider use by a user
     *
     * @param userName - Name of user
     * @return A list of providers objects.
     */
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideServiceImpl.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideServiceImpl.getUser(userName);
    }


}