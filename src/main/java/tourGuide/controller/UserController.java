package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.exception.userPreferencesAreEmpty;
import tourGuide.model.UpdateUserPreferences;
import tourGuide.service.TourGuideService;

@RestController
public class UserController {

    @Autowired
    private TourGuideService tourGuideService;
    @PutMapping("/users/updatePreferences")
    public String updateUserPreferences(@RequestParam String userName, @RequestBody UpdateUserPreferences userPreferences) {
        if (userPreferences == null) {
            throw new userPreferencesAreEmpty();
        }
        tourGuideService.linkUpdatePreferenceToAnExistingUser(userName, userPreferences);
        return JsonStream.serialize(tourGuideService.getUser(userName).getUserPreferences());
    }

}

