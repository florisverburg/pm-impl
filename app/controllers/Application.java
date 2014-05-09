package controllers;

import play.mvc.*;
import views.html.*;

/**
 * The application controller
 */
public class Application extends Controller {

    /**
     * Create the index page
     * @return The index page
     */
    public static Result index() {
       return ok(index.render("Your new application is ready."));
    }


}
