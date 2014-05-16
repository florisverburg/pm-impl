package controllers;

import models.*;
import play.Logger;
import play.mvc.*;
import views.html.*;

/**
 * Created by Marijn Goedegebure on 15-5-2014.
 * The class that handles all the actions used with the practicals
 */
public class PracticalController extends SecuredController{

    @Security.Authenticated(Secured.class)
    public static Result register(long id, String secret) {
        Practical practicalToRender = Practical.findById(id);
        if(practicalToRender != null) {
            if(!practicalToRender.getUsers().contains(getUser())) {
                if (practicalToRender.getSecret().equals(secret)) {
                    practicalToRender.addUsers(getUser());
                    practicalToRender.save();
                    return redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
                } else {
                    return redirect(routes.Application.about());
                    //redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
                }
            }
            else {
                Logger.debug("User is already coupled to this practical");
                return redirect(routes.Application.index());
            }
        }
        // When practical does not exist, show correct error
        else {
            Logger.debug("Practical does not exist");
            return redirect(routes.Application.contact());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result viewPractical(long id) {
        Practical practicalToRender = Practical.findById(id);
        if (practicalToRender != null) {
            return ok(viewPractical.render(practicalToRender));
        }
        else {
            Logger.debug("Practical does not exist");
            return redirect(routes.Application.index());
        }
    }
}
