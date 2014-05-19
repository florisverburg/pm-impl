package controllers;

import helpers.Secure;
import models.*;
import play.mvc.*;
import views.html.*;

/**
 * Created by Freek on 19/05/14.
 * Profile controller to edit, view and manage profiles
 */
@Secure.Authenticated
public class Profile extends Controller {

    /**
     * Shows the current users' profile
     * @return The profile page
     */
    public static Result view() {
        User user = Secure.getUser();
        return ok(profile.render(user));
    }
}
