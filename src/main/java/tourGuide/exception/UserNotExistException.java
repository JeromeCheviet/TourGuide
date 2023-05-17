package tourGuide.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User not found")
public class UserNotExistException extends RuntimeException {

    private Logger logger = LoggerFactory.getLogger(UserNotExistException.class);
    public UserNotExistException(String userName) {
        super("User " + userName + " not found");
        logger.warn("User " + userName + "not found");
    }
}
