package controllers;

import models.*;
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
            if (practicalToRender.getSecret().equals(secret)) {
                getUser().addPractical(practicalToRender);
                return redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
            } else {
                return redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
            }
        }
        // When practical does not exist, show correct error
        else {
            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result viewPractical(long id) {
        Practical practicalToRender = Practical.findById(id);
        if(practicalToRender != null)
            return ok(viewPractical.render(practicalToRender));
        else
            return redirect(routes.Application.index());
    }
}
