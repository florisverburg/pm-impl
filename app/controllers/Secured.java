package controllers;

import models.*;
import play.mvc.*;

/**
 * Created by Freek on 14/05/14.
 * This class contains all the security checks for the controllers
 */
public class Secured extends Security.Authenticator {

    /**
     * Retrieve the username (user id)
     * @param ctx The context of the current application
     * @return The username (user id)
     */
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get("user_id");
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
