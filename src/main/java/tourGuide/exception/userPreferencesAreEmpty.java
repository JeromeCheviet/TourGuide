package tourGuide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class userPreferencesAreEmpty extends RuntimeException {
    public userPreferencesAreEmpty() {
        super("User preferences are empty");
    }
}
