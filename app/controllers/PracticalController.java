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
        if(practicalToRender.getSecret().equals(secret)) {
            getUser().addPractical(practicalToRender);
            return redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
        }
        else {
            return redirect(routes.PracticalController.viewPractical(practicalToRender.getId()));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result viewPractical(long id) {
        Practical practicalToRender = Practical.findById(id);
        return ok(viewPractical.render(practicalToRender));
    }
}
