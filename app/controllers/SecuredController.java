package controllers;

import models.*;
import play.mvc.*;

/**
 * Created by Freek on 15/05/14.
 * Extends the default controller with additional functions for getting user information.
 */
public abstract class SecuredController extends Controller {

    /**
     * Get the currently logged in user id
     * @return The user id
     */
    protected Long getUserId() {
        String userId = request().username();
        return (userId == null)? null : Long.parseLong(userId);
    }

    /**
     * Get the currently logged in user
     * @return The user
     */
    protected User getUser() {
        Long userId = getUserId();
        return (userId == null)? null : User.findById(userId);
    }
}
