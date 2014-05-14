package controllers;

import models.*;
import play.mvc.*;

/**
 * Created by Freek on 14/05/14.
 * This class contains all the security checks for the controllers
 */
public class Secured extends Security.Authenticator {

    /**
     * Return the user id from a session
     * @param session The current session
     * @return The user id
     */
    private Long getUserId(Http.Session session) {
        String userId = session.get("user_id");
        return (userId == null)? null : Long.parseLong(userId);
    }

    /**
     * Retrieve the username (email address)
     * @param ctx The context of the current application
     * @return The username (email address)
     */
    @Override
    public String getUsername(Http.Context ctx) {
        // Search for the user by the user_id in the session
        Long userId = getUserId(ctx.session());
        return (userId == null)? null : User.findById(userId).getEmail();
    }

    /**
     * When user isn't authorized
     * @param ctx The context of the current application
     * @return Redirection to the login page
     */
    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Authentication.login());
    }
}
