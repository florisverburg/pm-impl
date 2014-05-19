package controllers;

import helpers.Secure;
import models.*;
import play.Logger;
import play.mvc.*;
import views.html.*;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 * The class that handles all the actions used with the practicals
 */
public class PracticalController extends Controller {

    /**
     * Method used to register a new practical to a user
     *
     * @param id     of the practical
     * @param secret of the practical
     * @return the appropriate view
     */
    @Secure.Authenticated
    public static Result register(long id, String secret) {
        Practical practicalToRender = Practical.findById(id);
        User user = Secure.getUser();
        // Checks of practical exist
        if(practicalToRender == null) {
            // When practical does not exist, show correct error
            flash("error", "practical.doesNotExist");
            return redirect(routes.Application.index());
        }
        else if (practicalToRender.getUsers().contains(user)){
            // Checks of the user is not already a part of the practical
            return redirect(routes.PracticalController.
                    viewPractical(practicalToRender.getId()));
        }
        else if (!practicalToRender.getSecret().equals(secret)) {
            // Checks if the secret is correct
            flash("error", "practical.wrongSecret");
            return redirect(routes.PracticalController.
                    viewPractical(practicalToRender.getId()));
        }
        // if everything is correct, add user to practical
        Practical.addUserToPractical(practicalToRender, Secure.getUser());
        return redirect(routes.PracticalController.
                viewPractical(practicalToRender.getId()));
    }

    /**
     * Method to view the practical specified by the url
     * @param id of the practical
     * @return appropriate url
     */
    @Secure.Authenticated
    public static Result viewPractical(long id) {
        Practical practicalToRender = Practical.findById(id);
        // If practical does not exist
        if (practicalToRender == null) {
            Logger.debug("Practical does not exist");
            flash("error", "practical.doesNotExist");
            return redirect(routes.Application.index());
        }
        else {
            Logger.debug("Practical does exist");
            return ok(viewPractical.render(practicalToRender));
        }
    }
}
