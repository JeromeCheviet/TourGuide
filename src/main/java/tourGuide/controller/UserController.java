package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.service.TourGuideService;

/**
 * Class which manage REST API Controller for User.
 */
@RestController
public class UserController {

    @Autowired
    private TourGuideService tourGuideService;

    /**
     * Method to update preferences for a user
     * @param userName - Name of user
     * @param userPreferences - user's preferences. Must contain all preferences and not only modified.
     * @return Object updateUserPreferences in Json format.
     */
    @PutMapping("/users/updatePreferences")
    public String updateUserPreferences(@RequestParam String userName, @RequestBody UpdateUserPreferences userPreferences) {
        tourGuideService.linkUpdatePreferenceToAnExistingUser(userName, userPreferences);
        return JsonStream.serialize(userPreferences);
    }

}

