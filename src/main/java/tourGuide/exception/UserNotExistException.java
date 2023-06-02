package tourGuide.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class which create and manage an UserNotExistException.
 * If this exception is throw, a bad request http status and the reason is sending to the controller.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User not found")
public class UserNotExistException extends RuntimeException {

    private Logger logger = LoggerFactory.getLogger(UserNotExistException.class);

    /**
     * Method use to construct an exception when no user is found.
     *
     * @param userName String whose contain the username not found.
     */
    public UserNotExistException(String userName) {
        super("User " + userName + " not found");
        logger.warn("User " + userName + "not found");
    }
}
